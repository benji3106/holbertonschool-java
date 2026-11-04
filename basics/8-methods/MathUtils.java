public class MathUtils {

    public static int max(int a, int b) {
        return (a > b) ? a : b;
    }

    public static int max(int a, int b, int c) {
        return max(max(a, b), c);
    }

    public static int max(int... values) {
        if (values == null || values.length == 0) {
            return Integer.MIN_VALUE;
        }
        int highest = values[0];
        for (int val : values) {
            if (val > highest) {
                highest = val;
            }
        }
        return highest;
    }

    public static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }
        for (int i = 2; i <= n / i; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int gcd(int a, int b) {
        long x = Math.abs((long) a);
        long y = Math.abs((long) b);
        while (y != 0) {
            long remainder = x % y;
            x = y;
            y = remainder;
        }
        return (int) x;
    }

    public static long power(int base, int exponent) {
        if (exponent < 0) {
            return -1;
        }
        long result = 1;
        long currentBase = base;
        int exp = exponent;

        while (exp > 0) {
            if ((exp & 1) == 1) {
                result *= currentBase;
            }
            currentBase *= currentBase;
            exp >>= 1;
        }
        return result;
    }

    public static int fibonacci(int n) {
        if (n < 0) {
            return -1;
        }
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void main(String[] args) {
        System.out.println("--- Testing max Overloading & Varargs ---");
        System.out.println("max(3, 9)       -> " + max(3, 9));
        System.out.println("max(5, 12, 4)   -> " + max(5, 12, 4));
        System.out.println("max(1, 2, 3, 4) -> " + max(1, 2, 3, 4));
        System.out.println("max() Empty     -> " + max());

        System.out.println("\n--- Testing isPrime ---");
        System.out.println("isPrime(1)          -> " + isPrime(1));
        System.out.println("isPrime(2)          -> " + isPrime(2));
        System.out.println("isPrime(2147483647) -> " + isPrime(2147483647));

        System.out.println("\n--- Testing gcd ---");
        System.out.println("gcd(0, 9)    -> " + gcd(0, 9));
        System.out.println("gcd(-24, 18) -> " + gcd(-24, 18));

        System.out.println("\n--- Testing power ---");
        System.out.println("power(2, 40) -> " + power(2, 40));
        System.out.println("power(5, -2) -> " + power(5, -2));

        System.out.println("\n--- Testing fibonacci (Recursive) ---");
        System.out.println("fibonacci(6)  -> " + fibonacci(6));
        System.out.println("fibonacci(-1) -> " + fibonacci(-1));
    }
}