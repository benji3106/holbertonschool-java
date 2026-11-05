public class Scope {

    public static void main(String[] args) {
        Counter counter = new Counter();
        counter.increment();
        counter.increment();
        counter.increment();
        System.out.println("Counter: " + counter.getValue());

        int x = 10;
        {
            int y = 5;
            x = x + y;
        }
        System.out.println("x = " + x);

        // y n'existe que dans le bloc ci-dessus : hors du bloc, il est inconnu.
        // System.out.println(y);
        //
        // 10-access/Scope.java:17: error: cannot find symbol
        //         System.out.println(y);
        //                            ^
        //   symbol:   variable y
        //   location: class Scope

        // value est private : une autre classe ne peut pas l'écrire directement.
        // counter.value = 42;
        //
        // 10-access/Scope.java:18: error: value has private access in Counter
        //         counter.value = 42;
        //                ^
    }
}