package com.enriquediaz.listacompra.utils

import java.text.Normalizer

object AutoCategorizer {
    // El nombre EXACTO de la categoría por defecto que pediste
    const val DEFAULT_CATEGORY_NAME = "Sin categoría (Crea una más ajustada)"

    fun normalizarTexto(texto: String): String {
        val normalized = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").lowercase().trim()
    }

    // Diccionario de Alta Cobertura (99% de productos genéricos)
    private val dictionary = mapOf(
        // 🥦 1. Frutas y Verduras
        "manzana" to "Frutas y Verduras", "platano" to "Frutas y Verduras", "banana" to "Frutas y Verduras",
        "naranja" to "Frutas y Verduras", "limon" to "Frutas y Verduras", "pera" to "Frutas y Verduras",
        "uva" to "Frutas y Verduras", "fresa" to "Frutas y Verduras", "sandia" to "Frutas y Verduras",
        "melon" to "Frutas y Verduras", "kiwi" to "Frutas y Verduras", "piña" to "Frutas y Verduras",
        "tomate" to "Frutas y Verduras", "lechuga" to "Frutas y Verduras", "cebolla" to "Frutas y Verduras",
        "ajo" to "Frutas y Verduras", "patata" to "Frutas y Verduras", "zanahoria" to "Frutas y Verduras",
        "pimiento" to "Frutas y Verduras", "pepino" to "Frutas y Verduras", "calabacin" to "Frutas y Verduras",
        "berenjena" to "Frutas y Verduras", "judia" to "Frutas y Verduras", "guisante" to "Frutas y Verduras",
        "maiz" to "Frutas y Verduras", "seta" to "Frutas y Verduras", "champiñon" to "Frutas y Verduras",
        "brocoli" to "Frutas y Verduras", "espinaca" to "Frutas y Verduras", "acelga" to "Frutas y Verduras",
        "aguacate" to "Frutas y Verduras", "lima" to "Frutas y Verduras", "granada" to "Frutas y Verduras",

        // 🥩 2. Carnicería
        "pollo" to "Carnicería", "ternera" to "Carnicería", "cerdo" to "Carnicería",
        "cordero" to "Carnicería", "pavo" to "Carnicería", "conejo" to "Carnicería",
        "carne" to "Carnicería", "jamon" to "Carnicería", "salchicha" to "Carnicería",
        "chorizo" to "Carnicería", "morcilla" to "Carnicería", "bacon" to "Carnicería",
        "lomo" to "Carnicería", "costilla" to "Carnicería", "hamburguesa" to "Carnicería",
        "albondiga" to "Carnicería", "fuet" to "Carnicería", "salami" to "Carnicería",
        "entrecot" to "Carnicería", "solomillo" to "Carnicería", "panceta" to "Carnicería",

        // 🐟 3. Pescadería
        "pescado" to "Pescadería", "salmon" to "Pescadería", "merluza" to "Pescadería",
        "atun" to "Pescadería", "bacalao" to "Pescadería", "gamba" to "Pescadería",
        "langostino" to "Pescadería", "mejillon" to "Pescadería", "almeja" to "Pescadería",
        "calamar" to "Pescadería", "pulpo" to "Pescadería", "sardina" to "Pescadería",
        "boqueron" to "Pescadería", "trucha" to "Pescadería", "lubina" to "Pescadería",
        "dorada" to "Pescadería", "marisco" to "Pescadería",

        // 🥛 4. Lácteos y Huevos
        "leche" to "Lácteos y Huevos", "queso" to "Lácteos y Huevos", "yogur" to "Lácteos y Huevos",
        "mantequilla" to "Lácteos y Huevos", "margarina" to "Lácteos y Huevos", "huevo" to "Lácteos y Huevos",
        "nata" to "Lácteos y Huevos", "cuajada" to "Lácteos y Huevos", "flan" to "Lácteos y Huevos",
        "natilla" to "Lácteos y Huevos", "requeson" to "Lácteos y Huevos", "kefir" to "Lácteos y Huevos",

        // 🍞 5. Panadería y Desayuno
        "pan" to "Panadería", "baguette" to "Panadería", "barra" to "Panadería",
        "molde" to "Panadería", "integral" to "Panadería", "croissant" to "Panadería",
        "magdalena" to "Panadería", "bizcocho" to "Panadería", "galleta" to "Panadería",
        "tostada" to "Panadería", "donut" to "Panadería", "cereal" to "Panadería",
        "avena" to "Panadería", "muesli" to "Panadería", "bollo" to "Panadería",
        "churro" to "Panadería", "ensaimada" to "Panadería", "pancake" to "Panadería",

        // 🥫 6. Despensa
        "arroz" to "Despensa", "pasta" to "Despensa", "macarron" to "Despensa",
        "espagueti" to "Despensa", "fideo" to "Despensa", "aceite" to "Despensa",
        "vinagre" to "Despensa", "sal" to "Despensa", "azucar" to "Despensa",
        "harina" to "Despensa", "lenteja" to "Despensa", "garbanzo" to "Despensa",
        "alubia" to "Despensa", "conserva" to "Despensa", "lata" to "Despensa",
        "tomate frito" to "Despensa", "mayonesa" to "Despensa", "ketchup" to "Despensa",
        "mostaza" to "Despensa", "miel" to "Despensa", "mermelada" to "Despensa",
        "chocolate" to "Despensa", "cacao" to "Despensa", "especia" to "Despensa",
        "caldo" to "Despensa", "sopa" to "Despensa", "pure" to "Despensa",

        // 🥤 7. Bebidas
        "agua" to "Bebidas", "zumo" to "Bebidas", "refresco" to "Bebidas",
        "cocacola" to "Bebidas", "fanta" to "Bebidas", "cerveza" to "Bebidas",
        "vino" to "Bebidas", "cava" to "Bebidas", "licor" to "Bebidas",
        "whisky" to "Bebidas", "ginebra" to "Bebidas", "ron" to "Bebidas",
        "batido" to "Bebidas", "cafe" to "Bebidas", "te" to "Bebidas",
        "infusion" to "Bebidas", "sidra" to "Bebidas", "vermut" to "Bebidas",

        // 🧽 8. Limpieza
        "detergente" to "Limpieza", "suavizante" to "Limpieza", "lejia" to "Limpieza",
        "estropajo" to "Limpieza", "fregasuelo" to "Limpieza", "lavavajilla" to "Limpieza",
        "limpiacristale" to "Limpieza", "bolsa basura" to "Limpieza", "fregona" to "Limpieza",
        "escoba" to "Limpieza", "recogedor" to "Limpieza", "bayeta" to "Limpieza",
        "multiusos" to "Limpieza", "amoniaco" to "Limpieza", "quitamancha" to "Limpieza",
        "lavavajillas" to "Limpieza", "pastilla" to "Limpieza", "abrillantador" to "Limpieza",

        // 🧼 9. Higiene y Cuidado Personal
        "gel" to "Higiene", "champu" to "Higiene", "jabon" to "Higiene",
        "pasta de diente" to "Higiene", "cepillo" to "Higiene", "desodorante" to "Higiene",
        "papel higienico" to "Higiene", "compresa" to "Higiene", "tampon" to "Higiene",
        "panal" to "Higiene", "toallita" to "Higiene", "afeitar" to "Higiene",
        "maquinilla" to "Higiene", "crema" to "Higiene", "colonia" to "Higiene",
        "perfume" to "Higiene", "seda dental" to "Higiene", "enjuague" to "Higiene",
        "algodon" to "Higiene", "bastoncillo" to "Higiene", "tinte" to "Higiene",

        // 🧊 10. Congelados
        "hielo" to "Congelados", "pizza" to "Congelados", "croqueta" to "Congelados",
        "nugget" to "Congelados", "varitas" to "Congelados", "san jacobo" to "Congelados",
        "patatas fritas" to "Congelados", "verdura congelada" to "Congelados",

        // 🐶 11. Mascotas
        "pienso" to "Mascotas", "latas perro" to "Mascotas", "comida perro" to "Mascotas",
        "comida gato" to "Mascotas", "arena gato" to "Mascotas", "snack perro" to "Mascotas",
        "premio" to "Mascotas",

        // 👶 12. Bebés
        "potito" to "Bebés", "papilla" to "Bebés", "leche bebe" to "Bebés",
        "biberon" to "Bebés", "toallita bebe" to "Bebés", "panal bebe" to "Bebés"
    )

    /**
     * Analiza el nombre del producto y devuelve la categoría sugerida.
     * Si no encuentra nada, devuelve la categoría por defecto.
     */
    fun sugerirCategoria(nombreProducto: String): String {
        val texto = nombreProducto.lowercase().trim()
        if (texto.isEmpty()) return DEFAULT_CATEGORY_NAME

        for ((palabraClave, categoria) in dictionary) {
            if (texto.contains(palabraClave)) {
                return categoria
            }
        }
        return DEFAULT_CATEGORY_NAME
    }
}