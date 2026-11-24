package com.example.myclosetapp.stylist

import com.example.myclosetapp.data.model.*
import java.util.concurrent.TimeUnit

class LocalRulesStylist : StylistEngine {

    override fun suggest(
        request: StylistRequest,
        closet: List<ClothingItem>,
        prefs: UserPreferences,
        recentWornLogs: List<WearLog>,
        limit: Int
    ): List<OutfitSuggestion> {
        if (closet.isEmpty()) return emptyList()

        val recentCutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(prefs.avoidDaysThreshold.toLong())
        val recentWornItemIds = recentWornLogs
            .filter { it.wornDate >= recentCutoff }
            .flatMap { it.itemIds }
            .toSet()

        val tops = closet.filter { it.category == Category.BLUSAS }
        val bottoms = closet.filter { it.category == Category.CALCAS_SAIAS }
        val dresses = closet.filter { it.category == Category.VESTIDOS }
        val coats = closet.filter { it.category == Category.CASACOS }
        val shoes = closet.filter { it.category == Category.CALCADOS }
        val bags = closet.filter { it.category == Category.BOLSAS }
        val accessories = closet.filter { it.category == Category.ACESSORIOS }

        val candidates = mutableListOf<List<ClothingItem>>()

        // Dress-based combinations
        for (dress in dresses) {
            val shoeList = if (shoes.isNotEmpty()) shoes else listOf(null)
            for (shoe in shoeList) {
                val itemCombo = mutableListOf<ClothingItem>()
                itemCombo.add(dress)
                if (shoe != null) itemCombo.add(shoe)

                // Optional coat for cold/rain
                if (request.weather == WeatherItem.FRIO || request.weather == WeatherItem.CHUVA) {
                    coats.firstOrNull()?.let { itemCombo.add(it) }
                }
                // Optional bag or accessory
                bags.firstOrNull()?.let { itemCombo.add(it) }
                candidates.add(itemCombo)
            }
        }

        // Two-piece combinations (Top + Bottom)
        for (top in tops) {
            for (bottom in bottoms) {
                val shoeList = if (shoes.isNotEmpty()) shoes else listOf(null)
                for (shoe in shoeList) {
                    val itemCombo = mutableListOf<ClothingItem>()
                    itemCombo.add(top)
                    itemCombo.add(bottom)
                    if (shoe != null) itemCombo.add(shoe)

                    // Optional coat for cold/rain/ameno
                    if (request.weather == WeatherItem.FRIO || request.weather == WeatherItem.CHUVA || request.weather == WeatherItem.AMENO) {
                        coats.firstOrNull()?.let { itemCombo.add(it) }
                    }

                    // Optional accessory/bag
                    val extra = bags.firstOrNull() ?: accessories.firstOrNull()
                    extra?.let { itemCombo.add(it) }

                    candidates.add(itemCombo)
                }
            }
        }

        // Fallback if no full combo could be formed (e.g. closet only has a few items)
        if (candidates.isEmpty()) {
            val simpleCombo = closet.take(3)
            if (simpleCombo.isNotEmpty()) {
                candidates.add(simpleCombo)
            }
        }

        val suggestions = candidates.map { items ->
            evaluateOutfit(items, request, prefs, recentWornItemIds)
        }

        // Distinct by item IDs set to avoid duplicate suggestions
        val uniqueSuggestions = suggestions
            .groupBy { suggestion -> suggestion.items.map { it.id }.sorted().joinToString(",") }
            .map { (_, group) -> group.maxByOrNull { it.score }!! }
            .sortedByDescending { it.score }

        return uniqueSuggestions.take(limit)
    }

    private fun evaluateOutfit(
        items: List<ClothingItem>,
        request: StylistRequest,
        prefs: UserPreferences,
        recentWornItemIds: Set<Long>
    ): OutfitSuggestion {
        var score = 50 // Base score
        val reasons = mutableListOf<String>()

        // 1. Occasion matching
        if (request.occasion != null) {
            val matchingItems = items.count { request.occasion in it.occasions }
            if (matchingItems > 0) {
                val pts = matchingItems * 15
                score += pts
                reasons.add("Adequado para ${request.occasion.label} (+${pts} pts)")
            } else {
                score -= 10
            }
        }

        // 2. Weather & Season matching
        when (request.weather) {
            WeatherItem.CALOR -> {
                val hasHeavyCoat = items.any { it.category == Category.CASACOS }
                if (hasHeavyCoat) {
                    score -= 30
                    reasons.add("Penalidade: casaco em dia de calor (-30 pts)")
                }
                val summerItems = items.count { SeasonItem.VERAO in it.seasons || SeasonItem.PRIMAVERA in it.seasons }
                if (summerItems > 0) {
                    score += 15
                    reasons.add("Peças leves e frescas para o calor (+15 pts)")
                }
            }
            WeatherItem.FRIO -> {
                val hasCoat = items.any { it.category == Category.CASACOS }
                if (hasCoat) {
                    score += 25
                    reasons.add("Inclui casaco/terceira peça para o frio (+25 pts)")
                } else {
                    score -= 15
                    reasons.add("Sem casaco para o frio (-15 pts)")
                }
            }
            WeatherItem.CHUVA -> {
                val hasCoat = items.any { it.category == Category.CASACOS }
                if (hasCoat) {
                    score += 20
                    reasons.add("Proteção com casaco para chuva (+20 pts)")
                }
            }
            WeatherItem.AMENO -> {
                score += 10
                reasons.add("Look equilibrado para clima ameno (+10 pts)")
            }
            null -> {}
        }

        // 3. Color Harmony
        val strongColors = items.count { !it.color.isNeutral }
        val neutralColors = items.count { it.color.isNeutral }

        if (strongColors == 0 && neutralColors > 0) {
            score += 20
            reasons.add("Paleta de cores neutras e elegante (+20 pts)")
        } else if (strongColors == 1) {
            score += 25
            reasons.add("Ponto de cor em destaque com base neutra (+25 pts)")
        } else if (strongColors >= 2) {
            score -= 15
            reasons.add("Múltiplas cores fortes combinadas (-15 pts)")
        }

        // 4. Pattern check
        val patternedItems = items.count { it.pattern != PatternItem.LISA }
        if (patternedItems == 1) {
            score += 15
            reasons.add("Estampa única em destaque (+15 pts)")
        } else if (patternedItems > 1) {
            score -= 25
            reasons.add("Conflito de estampas evitado (-25 pts)")
        } else {
            score += 10
            reasons.add("Visual clean sem estampas (+10 pts)")
        }

        // 5. User Preferences (Favorite styles & colors)
        var styleFavBonus = 0
        var colorFavBonus = 0
        items.forEach { item ->
            if (item.styles.any { it in prefs.favoriteStyles }) {
                styleFavBonus += 10
            }
            if (item.color in prefs.favoriteColors) {
                colorFavBonus += 10
            }
        }
        if (styleFavBonus > 0) {
            score += styleFavBonus
            reasons.add("Alinhado aos seus estilos favoritos (+${styleFavBonus} pts)")
        }
        if (colorFavBonus > 0) {
            score += colorFavBonus
            reasons.add("Inclui suas cores preferidas (+${colorFavBonus} pts)")
        }

        // 6. Recency Penalty
        val recentlyWornCount = items.count { it.id in recentWornItemIds }
        if (recentlyWornCount > 0) {
            val penalty = recentlyWornCount * 20
            score -= penalty
            reasons.add("Penalidade: contém peça usada nos últimos ${prefs.avoidDaysThreshold} dias (-${penalty} pts)")
        } else {
            score += 15
            reasons.add("Peças descansadas há mais de ${prefs.avoidDaysThreshold} dias (+15 pts)")
        }

        return OutfitSuggestion(
            items = items,
            score = score.coerceAtLeast(0),
            matchReasons = reasons
        )
    }
}
