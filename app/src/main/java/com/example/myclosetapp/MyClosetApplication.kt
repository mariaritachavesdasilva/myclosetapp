package com.example.myclosetapp

import android.app.Application
import com.example.myclosetapp.data.model.*
import com.example.myclosetapp.di.AppContainer
import com.example.myclosetapp.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyClosetApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        seedInitialData()
    }

    private fun seedInitialData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentCount = container.closetRepository.itemsCount.first()
                if (currentCount == 0) {
                    val sampleItems = listOf(
                        ClothingItem(
                            name = "Camisa Off-White de Seda",
                            category = Category.BLUSAS,
                            color = ColorItem.OFF_WHITE,
                            pattern = PatternItem.LISA,
                            styles = setOf(StyleItem.ELEGANTE, StyleItem.MINIMALISTA),
                            occasions = setOf(OccasionItem.TRABALHO, OccasionItem.PASSEIO, OccasionItem.ENCONTRO),
                            seasons = setOf(SeasonItem.PRIMAVERA, SeasonItem.VERAO, SeasonItem.OUTONO),
                            brand = "Zara",
                            notes = "Tecido super leve e confortável",
                            isFavorite = true
                        ),
                        ClothingItem(
                            name = "Calça Alfaiataria Bege",
                            category = Category.CALCAS_SAIAS,
                            color = ColorItem.BEGE,
                            pattern = PatternItem.LISA,
                            styles = setOf(StyleItem.ELEGANTE, StyleItem.FORMAL, StyleItem.MINIMALISTA),
                            occasions = setOf(OccasionItem.TRABALHO, OccasionItem.EVENTO_FORMAL, OccasionItem.PASSEIO),
                            seasons = setOf(SeasonItem.PRIMAVERA, SeasonItem.OUTONO, SeasonItem.INVERNO),
                            brand = "Mango",
                            notes = "Corte reto com pregas frontais",
                            isFavorite = true
                        ),
                        ClothingItem(
                            name = "Vestido Midi Floral",
                            category = Category.VESTIDOS,
                            color = ColorItem.ROSA,
                            pattern = PatternItem.FLORAL,
                            styles = setOf(StyleItem.ROMANTICO, StyleItem.CASUAL),
                            occasions = setOf(OccasionItem.PASSEIO, OccasionItem.ENCONTRO, OccasionItem.VIAGEM),
                            seasons = setOf(SeasonItem.PRIMAVERA, SeasonItem.VERAO),
                            brand = "Farm",
                            notes = "Modelagem fluida com amarração na cintura"
                        ),
                        ClothingItem(
                            name = "Blazer Estruturado Marrom",
                            category = Category.CASACOS,
                            color = ColorItem.MARROM,
                            pattern = PatternItem.LISA,
                            styles = setOf(StyleItem.ELEGANTE, StyleItem.FORMAL),
                            occasions = setOf(OccasionItem.TRABALHO, OccasionItem.EVENTO_FORMAL),
                            seasons = setOf(SeasonItem.OUTONO, SeasonItem.INVERNO),
                            brand = "AMARO",
                            notes = "Excelente para terceiras peças sofisticadas",
                            isFavorite = true
                        ),
                        ClothingItem(
                            name = "Mule Nude Minimalista",
                            category = Category.CALCADOS,
                            color = ColorItem.NUDE,
                            pattern = PatternItem.LISA,
                            styles = setOf(StyleItem.MINIMALISTA, StyleItem.ELEGANTE),
                            occasions = setOf(OccasionItem.TRABALHO, OccasionItem.PASSEIO, OccasionItem.ENCONTRO),
                            seasons = setOf(SeasonItem.PRIMAVERA, SeasonItem.VERAO, SeasonItem.OUTONO),
                            brand = "Arezzo"
                        ),
                        ClothingItem(
                            name = "Bolsa de Couro Marrom",
                            category = Category.BOLSAS,
                            color = ColorItem.MARROM,
                            pattern = PatternItem.LISA,
                            styles = setOf(StyleItem.CASUAL, StyleItem.ELEGANTE),
                            occasions = setOf(OccasionItem.TRABALHO, OccasionItem.PASSEIO, OccasionItem.FACULDADE),
                            seasons = setOf(SeasonItem.PRIMAVERA, SeasonItem.VERAO, SeasonItem.OUTONO, SeasonItem.INVERNO),
                            brand = "Schutz",
                            isFavorite = true
                        )
                    )

                    sampleItems.forEach { item ->
                        container.closetRepository.saveItem(item)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
