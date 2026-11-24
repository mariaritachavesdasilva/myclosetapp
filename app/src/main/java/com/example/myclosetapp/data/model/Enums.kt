package com.example.myclosetapp.data.model

enum class Category(val label: String) {
    BLUSAS("Blusas"),
    CALCAS_SAIAS("Calças e Saias"),
    VESTIDOS("Vestidos"),
    CASACOS("Casacos"),
    CALCADOS("Calçados"),
    BOLSAS("Bolsas"),
    ACESSORIOS("Acessórios")
}

enum class ColorItem(val label: String, val isNeutral: Boolean) {
    // Neutras
    PRETO("Preto", true),
    BRANCO("Branco", true),
    BEGE("Bege", true),
    CINZA("Cinza", true),
    MARROM("Marrom", true),
    OFF_WHITE("Off-White", true),
    NUDE("Nude", true),
    MARINHO("Marinho", true),

    // Fortes
    VERMELHO("Vermelho", false),
    AZUL("Azul", false),
    VERDE("Verde", false),
    AMARELO("Amarelo", false),
    ROSA("Rosa", false),
    ROXO("Roxo", false),
    LARANJA("Laranja", false),
    VINHO("Vinho", false)
}

enum class PatternItem(val label: String) {
    LISA("Lisa"),
    LISTRADA("Listrada"),
    XADREZ("Xadrez"),
    FLORAL("Floral"),
    POA("Poá"),
    ANIMAL_PRINT("Animal Print"),
    GEOMETRICA("Geométrica"),
    OUTRA("Outra")
}

enum class StyleItem(val label: String) {
    CASUAL("Casual"),
    FORMAL("Formal"),
    ELEGANTE("Elegante"),
    ESPORTIVO("Esportivo"),
    ROMANTICO("Romântico"),
    BOHO("Boho"),
    URBANO("Urbano"),
    MINIMALISTA("Minimalista")
}

enum class OccasionItem(val label: String) {
    TRABALHO("Trabalho"),
    FACULDADE("Faculdade"),
    PASSEIO("Passeio"),
    ENCONTRO("Encontro"),
    FESTA("Festa"),
    EVENTO_FORMAL("Evento Formal"),
    ACADEMIA("Academia"),
    VIAGEM("Viagem"),
    EM_CASA("Em Casa")
}

enum class SeasonItem(val label: String) {
    PRIMAVERA("Primavera"),
    VERAO("Verão"),
    OUTONO("Outono"),
    INVERNO("Inverno")
}

enum class WeatherItem(val label: String) {
    CALOR("Calor"),
    AMENO("Ameno"),
    FRIO("Frio"),
    CHUVA("Chuva")
}
