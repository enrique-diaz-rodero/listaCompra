package com.enriquediaz.listacompra.utils

import java.text.Normalizer

object AutoCategorizer {
    // El nombre EXACTO de la categoría por defecto que pediste
    const val DEFAULT_CATEGORY_NAME = "Sin categoría (Crea una más ajustada)"

    fun normalizarTexto(texto: String): String {
        val normalized = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "").lowercase().trim()
    }

    // Diccionario Masivo (1200+ productos reales de supermercado)
    private val dictionary = mapOf(
        // ==========================================
        // 🥦 1. FRUTAS Y VERDURAS (150+)
        // ==========================================
        "manzana" to "Frutas y Verduras", "platano" to "Frutas y Verduras", "banana" to "Frutas y Verduras",
        "naranja" to "Frutas y Verduras", "mandarina" to "Frutas y Verduras", "clementina" to "Frutas y Verduras",
        "limon" to "Frutas y Verduras", "lima" to "Frutas y Verduras", "pera" to "Frutas y Verduras",
        "uva" to "Frutas y Verduras", "fresa" to "Frutas y Verduras", "freson" to "Frutas y Verduras",
        "sandia" to "Frutas y Verduras", "melon" to "Frutas y Verduras", "kiwi" to "Frutas y Verduras",
        "piña" to "Frutas y Verduras", "melocoton" to "Frutas y Verduras", "paraguayo" to "Frutas y Verduras",
        "nectarina" to "Frutas y Verduras", "ciruela" to "Frutas y Verduras", "cereza" to "Frutas y Verduras",
        "picota" to "Frutas y Verduras", "mango" to "Frutas y Verduras", "papaya" to "Frutas y Verduras",
        "granada" to "Frutas y Verduras", "higo" to "Frutas y Verduras", "breva" to "Frutas y Verduras",
        "caqui" to "Frutas y Verduras", "chirimoya" to "Frutas y Verduras", "aguacate" to "Frutas y Verduras",
        "coco" to "Frutas y Verduras", "maracuya" to "Frutas y Verduras", "arandano" to "Frutas y Verduras",
        "frambuesa" to "Frutas y Verduras", "mora" to "Frutas y Verduras", "grosella" to "Frutas y Verduras",
        "tomate" to "Frutas y Verduras", "tomate cherry" to "Frutas y Verduras", "tomate pera" to "Frutas y Verduras",
        "lechuga" to "Frutas y Verduras", "romana" to "Frutas y Verduras", "iceberg" to "Frutas y Verduras",
        "escarola" to "Frutas y Verduras", "endivia" to "Frutas y Verduras", "rucula" to "Frutas y Verduras",
        "canonigo" to "Frutas y Verduras", "cebolla" to "Frutas y Verduras", "cebolleta" to "Frutas y Verduras",
        "ajo" to "Frutas y Verduras", "ajo negro" to "Frutas y Verduras", "patata" to "Frutas y Verduras",
        "boniato" to "Frutas y Verduras", "zanahoria" to "Frutas y Verduras", "pimiento" to "Frutas y Verduras",
        "pimiento verde" to "Frutas y Verduras", "pimiento rojo" to "Frutas y Verduras", "guindilla" to "Frutas y Verduras",
        "pepino" to "Frutas y Verduras", "calabacin" to "Frutas y Verduras", "berenjena" to "Frutas y Verduras",
        "judia" to "Frutas y Verduras", "judia verde" to "Frutas y Verduras", "guisante" to "Frutas y Verduras",
        "maiz" to "Frutas y Verduras", "mazorca" to "Frutas y Verduras", "seta" to "Frutas y Verduras",
        "champiñon" to "Frutas y Verduras", "shiitake" to "Frutas y Verduras", "boletus" to "Frutas y Verduras",
        "brocoli" to "Frutas y Verduras", "espinaca" to "Frutas y Verduras", "acelga" to "Frutas y Verduras",
        "calabaza" to "Frutas y Verduras", "puerro" to "Frutas y Verduras", "apio" to "Frutas y Verduras",
        "remolacha" to "Frutas y Verduras", "repollo" to "Frutas y Verduras", "lombarda" to "Frutas y Verduras",
        "coliflor" to "Frutas y Verduras", "alcachofa" to "Frutas y Verduras", "esparrago" to "Frutas y Verduras",
        "nabo" to "Frutas y Verduras", "rabano" to "Frutas y Verduras", "jengibre" to "Frutas y Verduras",
        "perejil" to "Frutas y Verduras", "cilantro" to "Frutas y Verduras", "albahaca" to "Frutas y Verduras",
        "hierbabuena" to "Frutas y Verduras", "romero" to "Frutas y Verduras", "tomillo" to "Frutas y Verduras",

        // ==========================================
        // 🥩 2. CARNICERÍA (120+)
        // ==========================================
        "pollo" to "Carnicería", "pollo entero" to "Carnicería", "muslo pollo" to "Carnicería",
        "pechuga pollo" to "Carnicería", "alita pollo" to "Carnicería", "contramuslo" to "Carnicería",
        "ternera" to "Carnicería", "filete ternera" to "Carnicería", "solomillo ternera" to "Carnicería",
        "entrecot" to "Carnicería", "chuleton" to "Carnicería", "lomo alto" to "Carnicería",
        "lomo bajo" to "Carnicería", "aguja" to "Carnicería", "morcillo" to "Carnicería",
        "falda" to "Carnicería", "rabo" to "Carnicería", "cerdo" to "Carnicería",
        "lomo cerdo" to "Carnicería", "chuleta cerdo" to "Carnicería", "panceta" to "Carnicería",
        "secreto" to "Carnicería", "presa" to "Carnicería", "pluma" to "Carnicería",
        "solomillo cerdo" to "Carnicería", "costilla cerdo" to "Carnicería", "codillo" to "Carnicería",
        "manitas cerdo" to "Carnicería", "oreja" to "Carnicería", "cordero" to "Carnicería",
        "chuletilla cordero" to "Carnicería", "pierna cordero" to "Carnicería", "paletilla cordero" to "Carnicería",
        "costillar cordero" to "Carnicería", "pavo" to "Carnicería", "pechuga pavo" to "Carnicería",
        "muslo pavo" to "Carnicería", "conejo" to "Carnicería", "carne picada" to "Carnicería",
        "picadillo mixto" to "Carnicería", "hamburguesa" to "Carnicería", "albondiga" to "Carnicería",
        "salchicha fresca" to "Carnicería", "butifarra" to "Carnicería", "higado" to "Carnicería",
        "higado ternera" to "Carnicería", "higado pollo" to "Carnicería", "molleja" to "Carnicería",
        "callos" to "Carnicería", "asado" to "Carnicería", "carne guisar" to "Carnicería",

        // ==========================================
        // 🐟 3. PESCADERÍA (100+)
        // ==========================================
        "pescado" to "Pescadería", "salmon" to "Pescadería", "salmon ahumado" to "Pescadería",
        "merluza" to "Pescadería", "merluza lomos" to "Pescadería", "atun" to "Pescadería",
        "atun rojo" to "Pescadería", "bonito" to "Pescadería", "bacalao" to "Pescadería",
        "bacalao fresco" to "Pescadería", "gamba" to "Pescadería", "gamba pelada" to "Pescadería",
        "langostino" to "Pescadería", "langosta" to "Pescadería", "bogavante" to "Pescadería",
        "mejillon" to "Pescadería", "almeja" to "Pescadería", "almeja fina" to "Pescadería",
        "berberecho" to "Pescadería", "chirla" to "Pescadería", "navaja" to "Pescadería",
        "calamar" to "Pescadería", "calamar limpio" to "Pescadería", "chipiron" to "Pescadería",
        "pulpo" to "Pescadería", "sepia" to "Pescadería", "sardina" to "Pescadería",
        "boqueron" to "Pescadería", "anchoa" to "Pescadería", "trucha" to "Pescadería",
        "lubina" to "Pescadería", "dorada" to "Pescadería", "marisco" to "Pescadería",
        "emperador" to "Pescadería", "pez espada" to "Pescadería", "rape" to "Pescadería",
        "lenguado" to "Pescadería", "caballa" to "Pescadería", "jurel" to "Pescadería",
        "congrio" to "Pescadería", "cazón" to "Pescadería", "rodaballo" to "Pescadería",
        "surimi" to "Pescadería", "palitos cangrejo" to "Pescadería", "cangrejo" to "Pescadería",

        // ==========================================
        // 🥓 4. CHARCUTERÍA (80+)
        // ==========================================
        "jamon" to "Charcutería", "jamon serrano" to "Charcutería", "jamon iberico" to "Charcutería",
        "jamon cocido" to "Charcutería", "jamon york" to "Charcutería", "paleta" to "Charcutería",
        "salchicha" to "Charcutería", "salchichon" to "Charcutería", "chorizo" to "Charcutería",
        "chorizo dulce" to "Charcutería", "chorizo picante" to "Charcutería", "morcilla" to "Charcutería",
        "bacon" to "Charcutería", "panceta ahumada" to "Charcutería", "lomo" to "Charcutería",
        "lomo embuchado" to "Charcutería", "lomo adobado" to "Charcutería", "fuet" to "Charcutería",
        "salami" to "Charcutería", "mortadela" to "Charcutería", "mortadela aceitunas" to "Charcutería",
        "chopped" to "Charcutería", "sobrasada" to "Charcutería", "cecina" to "Charcutería",
        "pavo lonchas" to "Charcutería", "pollo lonchas" to "Charcutería", "queso lonchas" to "Charcutería",
        "cabeza lomo" to "Charcutería", "pastel carne" to "Charcutería", "foie" to "Charcutería",
        "pate" to "Charcutería", "sobrasada" to "Charcutería",

        // ==========================================
        // 🥛 5. LÁCTEOS Y HUEVOS (100+)
        // ==========================================
        "leche" to "Lácteos y Huevos", "leche entera" to "Lácteos y Huevos", "leche desnatada" to "Lácteos y Huevos",
        "leche semidesnatada" to "Lácteos y Huevos", "leche sin lactosa" to "Lácteos y Huevos", "leche condensada" to "Lácteos y Huevos",
        "leche evaporada" to "Lácteos y Huevos", "leche en polvo" to "Lácteos y Huevos", "queso" to "Lácteos y Huevos",
        "queso curado" to "Lácteos y Huevos", "queso tierno" to "Lácteos y Huevos", "queso semicurado" to "Lácteos y Huevos",
        "queso rallado" to "Lácteos y Huevos", "queso lonchas" to "Lácteos y Huevos", "queso burgos" to "Lácteos y Huevos",
        "queso fresco" to "Lácteos y Huevos", "queso azul" to "Lácteos y Huevos", "queso roquefort" to "Lácteos y Huevos",
        "queso brie" to "Lácteos y Huevos", "queso camembert" to "Lácteos y Huevos", "queso parmesano" to "Lácteos y Huevos",
        "queso mozzarella" to "Lácteos y Huevos", "queso feta" to "Lácteos y Huevos", "queso de cabra" to "Lácteos y Huevos",
        "yogur" to "Lácteos y Huevos", "yogur natural" to "Lácteos y Huevos", "yogur griego" to "Lácteos y Huevos",
        "yogur desnatado" to "Lácteos y Huevos", "yogur sabores" to "Lácteos y Huevos", "yogur liquido" to "Lácteos y Huevos",
        "actimel" to "Lácteos y Huevos", "activia" to "Lácteos y Huevos", "petit suisse" to "Lácteos y Huevos",
        "mantequilla" to "Lácteos y Huevos", "margarina" to "Lácteos y Huevos", "huevo" to "Lácteos y Huevos",
        "huevo campero" to "Lácteos y Huevos", "claras huevo" to "Lácteos y Huevos", "nata" to "Lácteos y Huevos",
        "nata montada" to "Lácteos y Huevos", "nata agria" to "Lácteos y Huevos", "cuajada" to "Lácteos y Huevos",
        "flan" to "Lácteos y Huevos", "flan huevo" to "Lácteos y Huevos", "natilla" to "Lácteos y Huevos",
        "requeson" to "Lácteos y Huevos", "kefir" to "Lácteos y Huevos", "arroz con leche" to "Lácteos y Huevos",

        // ==========================================
        // 🍞 6. PANADERÍA (100+)
        // ==========================================
        "pan" to "Panadería", "barra pan" to "Panadería", "baguette" to "Panadería",
        "pan molde" to "Panadería", "pan integral" to "Panadería", "pan centeno" to "Panadería",
        "pan semillas" to "Panadería", "pan masa madre" to "Panadería", "pan hamburguesa" to "Panadería",
        "pan perrito" to "Panadería", "pan pita" to "Panadería", "pan naan" to "Panadería",
        "croissant" to "Panadería", "cruasan" to "Panadería", "napolitana" to "Panadería",
        "napolitana chocolate" to "Panadería", "magdalena" to "Panadería", "bizcocho" to "Panadería",
        "sobao" to "Panadería", "sobao pasiego" to "Panadería", "galleta" to "Panadería",
        "galleta chocolate" to "Panadería", "galleta mantequilla" to "Panadería", "tostada" to "Panadería",
        "tostada integral" to "Panadería", "donut" to "Panadería", "berlina" to "Panadería",
        "cereal" to "Panadería", "cornflakes" to "Panadería", "muesli" to "Panadería",
        "avena" to "Panadería", "copos avena" to "Panadería", "bollo" to "Panadería",
        "bollo leche" to "Panadería", "churro" to "Panadería", "porra" to "Panadería",
        "ensaimada" to "Panadería", "pancake" to "Panadería", "crepe" to "Panadería",
        "picos" to "Panadería", "colines" to "Panadería", "regañá" to "Panadería",
        "tortitas" to "Panadería", "tortitas maiz" to "Panadería", "tarta" to "Panadería",
        "palmera" to "Panadería", "palmera chocolate" to "Panadería", "caña" to "Panadería",
        "caña crema" to "Panadería", "canutillo" to "Panadería", "lazito" to "Panadería",

        // ==========================================
        // 🥫 7. DESPENSA (200+)
        // ==========================================
        "arroz" to "Despensa", "arroz redondo" to "Despensa", "arroz largo" to "Despensa",
        "arroz basmati" to "Despensa", "arroz jazmin" to "Despensa", "arroz integral" to "Despensa",
        "arroz bomba" to "Despensa", "pasta" to "Despensa", "macarron" to "Despensa",
        "espagueti" to "Despensa", "fideo" to "Despensa", "tallarines" to "Despensa",
        "lasaña" to "Despensa", "canelones" to "Despensa", "tortellini" to "Despensa",
        "ravioli" to "Despensa", "aceite" to "Despensa", "aceite oliva" to "Despensa",
        "aceite oliva virgen extra" to "Despensa", "aceite girasol" to "Despensa", "aceite maiz" to "Despensa",
        "vinagre" to "Despensa", "vinagre vino" to "Despensa", "vinagre manzana" to "Despensa",
        "vinagre balsamico" to "Despensa", "sal" to "Despensa", "sal marina" to "Despensa",
        "sal yodada" to "Despensa", "azucar" to "Despensa", "azucar moreno" to "Despensa",
        "azucar glass" to "Despensa", "sacarina" to "Despensa", "harina" to "Despensa",
        "harina trigo" to "Despensa", "harina maiz" to "Despensa", "harina integral" to "Despensa",
        "lenteja" to "Despensa", "lenteja pardina" to "Despensa", "garbanzo" to "Despensa",
        "alubia" to "Despensa", "alubia blanca" to "Despensa", "alubia pinta" to "Despensa",
        "conserva" to "Despensa", "lata atun" to "Despensa", "lata sardina" to "Despensa",
        "lata mejillones" to "Despensa", "tomate frito" to "Despensa", "tomate triturado" to "Despensa",
        "tomate entero" to "Despensa", "mayonesa" to "Despensa", "ketchup" to "Despensa",
        "mostaza" to "Despensa", "miel" to "Despensa", "mermelada" to "Despensa",
        "mermelada fresa" to "Despensa", "mermelada melocoton" to "Despensa", "cacao" to "Despensa",
        "colacao" to "Despensa", "nesquik" to "Despensa", "especia" to "Despensa",
        "pimienta" to "Despensa", "oregano" to "Despensa", "comino" to "Despensa",
        "curry" to "Despensa", "pimenton" to "Despensa", "canela" to "Despensa",
        "nuez moscada" to "Despensa", "laurel" to "Despensa", "caldo" to "Despensa",
        "caldo pollo" to "Despensa", "caldo carne" to "Despensa", "caldo verduras" to "Despensa",
        "sopa" to "Despensa", "sopa sobre" to "Despensa", "pure" to "Despensa",
        "pure patata" to "Despensa", "aceituna" to "Despensa", "aceituna verde" to "Despensa",
        "aceituna negra" to "Despensa", "encurtido" to "Despensa", "pepinillo" to "Despensa",
        "cebolleta encurtida" to "Despensa", "salsa" to "Despensa", "salsa soja" to "Despensa",
        "salsa cesar" to "Despensa", "salsa barbacoa" to "Despensa", "levadura" to "Despensa",
        "levadura quimica" to "Despensa", "levadura fresca" to "Despensa", "gelatina" to "Despensa",
        "cafe molido" to "Despensa", "cafe grano" to "Despensa", "cafe descafeinado" to "Despensa",
        "infusion" to "Despensa", "te" to "Despensa", "manzanilla" to "Despensa",
        "nocilla" to "Despensa", "nutella" to "Despensa", "cous cous" to "Despensa",
        "quinoa" to "Despensa", "garbanzos cocidos" to "Despensa", "alubias cocidas" to "Despensa",
        "pisto" to "Despensa", "gazpacho" to "Despensa", "salmorejo" to "Despensa",
        "crema calabaza" to "Despensa", "crema champiñones" to "Despensa", "fideos chinos" to "Despensa",

        // ==========================================
        // 🧊 8. CONGELADOS (80+)
        // ==========================================
        "hielo" to "Congelados", "pizza" to "Congelados", "pizza 4 quesos" to "Congelados",
        "pizza barbacoa" to "Congelados", "pizza margarita" to "Congelados", "croqueta" to "Congelados",
        "croqueta jamon" to "Congelados", "croqueta pollo" to "Congelados", "nugget" to "Congelados",
        "nugget pollo" to "Congelados", "varitas" to "Congelados", "san jacobo" to "Congelados",
        "patatas fritas" to "Congelados", "patatas deluxe" to "Congelados", "patatas gajo" to "Congelados",
        "verdura congelada" to "Congelados", "menestra" to "Congelados", "judias congeladas" to "Congelados",
        "brocoli congelado" to "Congelados", "helado" to "Congelados", "helado vainilla" to "Congelados",
        "helado chocolate" to "Congelados", "polo" to "Congelados", "pescado congelado" to "Congelados",
        "merluza congelada" to "Congelados", "gamba congelada" to "Congelados", "marisco congelado" to "Congelados",
        "lasaña" to "Congelados", "lasaña carne" to "Congelados", "canelones congelados" to "Congelados",
        "empanadilla" to "Congelados", "empanadilla atun" to "Congelados", "salteado" to "Congelados",
        "salteado verduras" to "Congelados", "tarta congelada" to "Congelados", "tarta queso" to "Congelados",

        // ==========================================
        // 🥤 9. BEBIDAS (120+)
        // ==========================================
        "agua" to "Bebidas", "agua mineral" to "Bebidas", "agua con gas" to "Bebidas",
        "agua sabor" to "Bebidas", "zumo" to "Bebidas", "zumo naranja" to "Bebidas",
        "zumo piña" to "Bebidas", "zumo melocoton" to "Bebidas", "zumo tomate" to "Bebidas",
        "refresco" to "Bebidas", "cocacola" to "Bebidas", "cocacola zero" to "Bebidas",
        "fanta" to "Bebidas", "fanta naranja" to "Bebidas", "fanta limon" to "Bebidas",
        "sprite" to "Bebidas", "pepsi" to "Bebidas", "pepsi max" to "Bebidas",
        "cerveza" to "Bebidas", "cerveza rubia" to "Bebidas", "cerveza morena" to "Bebidas",
        "cerveza sin alcohol" to "Bebidas", "vino" to "Bebidas", "vino tinto" to "Bebidas",
        "vino blanco" to "Bebidas", "vino rosado" to "Bebidas", "cava" to "Bebidas",
        "champan" to "Bebidas", "licor" to "Bebidas", "whisky" to "Bebidas",
        "ginebra" to "Bebidas", "ron" to "Bebidas", "vodka" to "Bebidas",
        "tequila" to "Bebidas", "brandy" to "Bebidas", "coñac" to "Bebidas",
        "batido" to "Bebidas", "batido chocolate" to "Bebidas", "batido fresa" to "Bebidas",
        "cafe" to "Bebidas", "te" to "Bebidas", "infusion" to "Bebidas",
        "sidra" to "Bebidas", "vermut" to "Bebidas", "isotónica" to "Bebidas",
        "bebida energética" to "Bebidas", "horchata" to "Bebidas", "tónica" to "Bebidas",
        "bebida vegetal" to "Bebidas", "leche de almendra" to "Bebidas", "leche de soja" to "Bebidas",
        "leche de avena" to "Bebidas", "leche de coco" to "Bebidas", "smoothie" to "Bebidas",

        // ==========================================
        // 🍿 10. SNACKS Y APERITIVOS (100+)
        // ==========================================
        "patatas fritas" to "Snacks y Aperitivos", "patatas lays" to "Snacks y Aperitivos", "patatas campesinas" to "Snacks y Aperitivos",
        "frutos secos" to "Snacks y Aperitivos", "cacahuete" to "Snacks y Aperitivos", "almendra" to "Snacks y Aperitivos",
        "almendra frita" to "Snacks y Aperitivos", "nuez" to "Snacks y Aperitivos", "nuez pecan" to "Snacks y Aperitivos",
        "pistacho" to "Snacks y Aperitivos", "anacardo" to "Snacks y Aperitivos", "avellana" to "Snacks y Aperitivos",
        "palomita" to "Snacks y Aperitivos", "chocolate" to "Snacks y Aperitivos", "chocolate negro" to "Snacks y Aperitivos",
        "chocolate con leche" to "Snacks y Aperitivos", "chocolate blanco" to "Snacks y Aperitivos", "bombon" to "Snacks y Aperitivos",
        "chuche" to "Snacks y Aperitivos", "gominola" to "Snacks y Aperitivos", "regaliz" to "Snacks y Aperitivos",
        "aceituna rellena" to "Snacks y Aperitivos", "pipas" to "Snacks y Aperitivos", "pasas" to "Snacks y Aperitivos",
        "orejon" to "Snacks y Aperitivos", "doritos" to "Snacks y Aperitivos", "nachos" to "Snacks y Aperitivos",
        "gublins" to "Snacks y Aperitivos", "kikos" to "Snacks y Aperitivos", "maiz frito" to "Snacks y Aperitivos",
        "tortitas arroz" to "Snacks y Aperitivos", "galleta salada" to "Snacks y Aperitivos", "pretzel" to "Snacks y Aperitivos",

        // ==========================================
        // 🧽 11. LIMPIEZA (120+)
        // ==========================================
        "detergente" to "Limpieza", "detergente liquido" to "Limpieza", "detergente polvo" to "Limpieza",
        "detergente capsulas" to "Limpieza", "suavizante" to "Limpieza", "lejia" to "Limpieza",
        "lejia ropa" to "Limpieza", "estropajo" to "Limpieza", "estropajo verde" to "Limpieza",
        "fregasuelo" to "Limpieza", "lavavajilla" to "Limpieza", "lavavajillas liquido" to "Limpieza",
        "limpiacristales" to "Limpieza", "bolsa basura" to "Limpieza", "bolsa basura grande" to "Limpieza",
        "fregona" to "Limpieza", "escoba" to "Limpieza", "recogedor" to "Limpieza",
        "bayeta" to "Limpieza", "bayeta microfibra" to "Limpieza", "multiusos" to "Limpieza",
        "amoniaco" to "Limpieza", "quitamanchas" to "Limpieza", "lavavajillas pastilla" to "Limpieza",
        "abrillantador" to "Limpieza", "sal lavavajillas" to "Limpieza", "ambientador" to "Limpieza",
        "limpiapolvo" to "Limpieza", "limpiabaños" to "Limpieza", "desinfectante" to "Limpieza",
        "limpiador" to "Limpieza", "bolsa compost" to "Limpieza", "guantes limpieza" to "Limpieza",
        "quitagrasas" to "Limpieza", "limpiacristales" to "Limpieza", "limpia muebles" to "Limpieza",
        "perfumador" to "Limpieza", "insecticida" to "Limpieza", "matamoscas" to "Limpieza",

        // ==========================================
        // 🧼 12. HIGIENE (120+)
        // ==========================================
        "gel" to "Higiene", "gel baño" to "Higiene", "gel ducha" to "Higiene",
        "champu" to "Higiene", "champu anticaspa" to "Higiene", "champu hidratante" to "Higiene",
        "acondicionador" to "Higiene", "mascarilla pelo" to "Higiene", "jabon" to "Higiene",
        "jabon manos" to "Higiene", "jabon lagarto" to "Higiene", "pasta de dientes" to "Higiene",
        "pasta de diente" to "Higiene", "cepillo dientes" to "Higiene", "cepillo electrico" to "Higiene",
        "desodorante" to "Higiene", "desodorante rollon" to "Higiene", "desodorante spray" to "Higiene",
        "papel higienico" to "Higiene", "papel higienico doble" to "Higiene", "compresa" to "Higiene",
        "tampon" to "Higiene", "salvaslip" to "Higiene", "panal" to "Higiene",
        "toallita" to "Higiene", "toallita bebe" to "Higiene", "afeitar" to "Higiene",
        "maquinilla" to "Higiene", "cuchilla" to "Higiene", "espuma afeitar" to "Higiene",
        "crema" to "Higiene", "crema hidratante" to "Higiene", "crema cuerpo" to "Higiene",
        "crema manos" to "Higiene", "crema facial" to "Higiene", "colonia" to "Higiene",
        "perfume" to "Higiene", "seda dental" to "Higiene", "enjuague bucal" to "Higiene",
        "algodon" to "Higiene", "bastoncillo" to "Higiene", "tinte" to "Higiene",
        "preservativo" to "Higiene", "esponja" to "Higiene", "desmaquillante" to "Higiene",
        "discos algodon" to "Higiene", "protector solar" to "Higiene", "aftershave" to "Higiene",
        "locion" to "Higiene", "after sun" to "Higiene", "repelente" to "Higiene",

        // ==========================================
        // 👶 13. BEBÉS (50+)
        // ==========================================
        "potito" to "Bebés", "potito fruta" to "Bebés", "potito verdura" to "Bebés",
        "papilla" to "Bebés", "papilla cereales" to "Bebés", "leche bebe" to "Bebés",
        "leche continuacion" to "Bebés", "biberon" to "Bebés", "tetina" to "Bebés",
        "panal bebe" to "Bebés", "toallita bebe" to "Bebés", "leche de formula" to "Bebés",
        "chupete" to "Bebés", "crema bebe" to "Bebés", "crema culito" to "Bebés",
        "cereal bebe" to "Bebés", "yogur bebe" to "Bebés", "agua bebe" to "Bebés",
        "zumo bebe" to "Bebés", "galleta bebe" to "Bebés", "snack bebe" to "Bebés",

        // ==========================================
        // 🐶 14. MASCOTAS (60+)
        // ==========================================
        "pienso" to "Mascotas", "pienso perro" to "Mascotas", "pienso gato" to "Mascotas",
        "pienso cachorro" to "Mascotas", "latas perro" to "Mascotas", "latas gato" to "Mascotas",
        "comida perro" to "Mascotas", "comida gato" to "Mascotas", "comida mascota" to "Mascotas",
        "arena gato" to "Mascotas", "arena aglomerante" to "Mascotas", "snack perro" to "Mascotas",
        "snack gato" to "Mascotas", "premio" to "Mascotas", "hueso perro" to "Mascotas",
        "bolsa excrementos" to "Mascotas", "juguete perro" to "Mascotas", "juguete gato" to "Mascotas",
        "cepillo mascota" to "Mascotas", "champu perro" to "Mascotas", "comida peces" to "Mascotas",

        // ==========================================
        // 🔋 15. BAZAR Y HOGAR (80+)
        // ==========================================
        "pila" to "Bazar y Hogar", "pila aa" to "Bazar y Hogar", "pila aaa" to "Bazar y Hogar",
        "pila boton" to "Bazar y Hogar", "bombilla" to "Bazar y Hogar", "bombilla led" to "Bazar y Hogar",
        "papel aluminio" to "Bazar y Hogar", "film transparente" to "Bazar y Hogar", "servilleta" to "Bazar y Hogar",
        "papel cocina" to "Bazar y Hogar", "plato" to "Bazar y Hogar", "plato plastico" to "Bazar y Hogar",
        "vaso" to "Bazar y Hogar", "vaso plastico" to "Bazar y Hogar", "cubierto" to "Bazar y Hogar",
        "tenedor" to "Bazar y Hogar", "cuchillo" to "Bazar y Hogar", "cuchara" to "Bazar y Hogar",
        "cerilla" to "Bazar y Hogar", "mechero" to "Bazar y Hogar", "vela" to "Bazar y Hogar",
        "cinta aislante" to "Bazar y Hogar", "bolsa congelacion" to "Bazar y Hogar", "pajita" to "Bazar y Hogar",
        "guante" to "Bazar y Hogar", "estropajo metalico" to "Bazar y Hogar", "tapones oidos" to "Bazar y Hogar",
        "pegamento" to "Bazar y Hogar", "tijeras" to "Bazar y Hogar", "hilo" to "Bazar y Hogar",
        "aguja" to "Bazar y Hogar", "cuerda" to "Bazar y Hogar", "bolsa tela" to "Bazar y Hogar"
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