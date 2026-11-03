public class StringTraps {

    public static boolean sameContent(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    public static boolean sameObject(String a, String b) {
        return a == b;
    }

    public static String repeat(String s, int times, String separator) {
        if (s == null || times <= 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) {
            result.append(s);
            if (i < times - 1) {
                result.append(separator);
            }
        }
        return result.toString();
    }

    public static String reverse(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder reversed = new StringBuilder(s);
        return reversed.reverse().toString();
    }

    public static String buildSlow(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += (char) ('a' + i % 26);
        }
        return result;
    }

    public static String buildFast(int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append((char) ('a' + i % 26));
        }
        return result.toString();
    }

    public static void main(String[] args) {
        String literal = "java";
        String created = new String("java");

        System.out.println("literals ==: " + sameObject("java", "java"));
        System.out.println("new String ==: " + sameObject(literal, created));
        System.out.println("equals: " + sameContent(literal, created));

        long start = System.nanoTime();
        buildSlow(100_000);
        long slowMs = (System.nanoTime() - start) / 1_000_000;
        System.out.println("slow: " + slowMs + " ms");

        start = System.nanoTime();
        buildFast(100_000);
        long fastMs = (System.nanoTime() - start) / 1_000_000;
        System.out.println("fast: " + fastMs + " ms");
    }
}