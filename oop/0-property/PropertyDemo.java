public class PropertyDemo {
    public static void main(String[] args) {
        Property p1 = new Property("P-001", "12 Rue de la Paix, 75002 Paris", 25, 950);
        Property p2 = new Property("P-002", "8 Chemin des Vignes, 33000 Bordeaux", 120, 1800);

        System.out.println(p1.getReference() + ": " + p1.getSurface() + " m2, " 
                + p1.getMonthlyRent() + " EUR/month, " + p1.getAnnualRent() + " EUR/year");
                
        System.out.println(p2.getReference() + ": " + p2.getSurface() + " m2, " 
                + p2.getMonthlyRent() + " EUR/month, " + p2.getAnnualRent() + " EUR/year");
    }
}