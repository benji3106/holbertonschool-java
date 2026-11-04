public class Scope {
    public static void main(String[] args) {
        // 1. Utilisation du Counter
        Counter counter = new Counter();
        counter.increment();
        counter.increment();
        counter.increment();
        System.out.println("Counter: " + counter.getValue());

        // 2. Bloc de portée (Scope)
        int x = 10;
        {
            int y = 5;
            x = x + y;
        }
        System.out.println("x = " + x);

        // 3. Tentative d'accès à 'y' hors de son bloc
        // System.out.println(y);
        // Error: cannot find symbol
        //   symbol:   variable y
        //   location: class access.Scope

        // 4. Tentative d'accès au champ privé 'value'
        // counter.value = 42;
        // Error: value has private access in access.Counter
    }
}