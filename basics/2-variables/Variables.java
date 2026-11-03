public class Variables {

    public static void main(String[] args) {
        // Partie 1 : une ligne printf par type
        System.out.printf("byte: %d %d%n", Byte.MIN_VALUE, Byte.MAX_VALUE);
        System.out.printf("short: %d %d%n", Short.MIN_VALUE, Short.MAX_VALUE);
        System.out.printf("int: %d %d%n", Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.printf("long: %d %d%n", Long.MIN_VALUE, Long.MAX_VALUE);
        System.out.printf("float: %s %s%n", Float.MIN_VALUE, Float.MAX_VALUE);
        System.out.printf("double: %s %s%n", Double.MIN_VALUE, Double.MAX_VALUE);
        System.out.printf("char: %d %d%n", (int) Character.MIN_VALUE, (int) Character.MAX_VALUE);
        System.out.printf("boolean: %b %b%n", false, true);

        // Partie 2 : pour chaque démonstration, un commentaire puis le calcul

        // OVERFLOW : Ajouter 1 à la valeur entière maximale (Integer.MAX_VALUE) dépasse la capacité de stockage en mémoire et fait basculer le résultat en négatif.
        int result = Integer.MAX_VALUE + 1;
        System.out.printf("overflow: %d%n", result);

        // --- PRECISION : Additionner 0.1 et 0.2 avec des nombres à virgule donne un léger décalage à cause de la façon dont l'ordinateur traduit les décimales en binaire.
        double sum = 0.1 + 0.2;
        System.out.printf("precision: %s%n", sum);

        // --- TRUNCATION : Forcer un nombre à virgule en entier ((int)) supprime tout simplement la partie après la virgule sans faire d'arrondi.
        int cut = (int) 3.99;
        System.out.printf("truncation: %d%n", cut);

        // --- PROMOTION : Ajouter 1L (un nombre long) force Java à élargir le calcul sur 64 bits, ce qui évite le dépassement et donne le vrai résultat mathématique.
        int start = Integer.MAX_VALUE;
        long total = start + 1L;
        System.out.printf("promotion: %d%n", total);
    }
}
