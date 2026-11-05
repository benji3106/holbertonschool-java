public class StaticDemo {
    public static void main(String[] args) {
        // 1. Affiche le nombre total de tickets avant toute création
        System.out.println("Before: " + Ticket.getTotalTickets());

        // 2. Crée deux tickets et affiche leurs codes séparés par un espace
        Ticket t1 = new Ticket();
        Ticket t2 = new Ticket();
        System.out.println(t1.getCode() + " " + t2.getCode());

        // 3. Affiche le nombre total après les créations
        System.out.println("After: " + Ticket.getTotalTickets());

        // 4. Ligne utilisant 'this' depuis main : main est static, il n'y a pas d'objet courant.
        // System.out.println(this);
        //
        // 11-static/StaticDemo.java:15: error: non-static variable this cannot be referenced from a static context
        //         System.out.println(this);
        //                            ^
    }
}