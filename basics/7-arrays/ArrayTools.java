public class ArrayTools {

    public static int max(int[] t) {
        int highest = t[0];
        for (int i = 1; i < t.length; i++) {
            if (t[i] > highest) {
                highest = t[i];
            }
        }
        return highest;
    }

    public static int min(int[] t) {
        int lowest = t[0];
        for (int i = 1; i < t.length; i++) {
            if (t[i] < lowest) {
                lowest = t[i];
            }
        }
        return lowest;
    }

    public static double average(int[] t) {
        if (t.length == 0) {
            return 0.0;
        }
        double sum = 0;
        for (int value : t) {
            sum += value;
        }
        return sum / t.length;
    }

    public static void reverseInPlace(int[] t) {
        int left = 0;
        int right = t.length - 1;
        while (left < right) {
            int temp = t[left];
            t[left] = t[right];
            t[right] = temp;
            left++;
            right--;
        }
    }

    public static int indexOf(int[] t, int value) {
        for (int i = 0; i < t.length; i++) {
            if (t[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static int[][] identity(int n) {
        int[][] id = new int[n][n];
        for (int i = 0; i < n; i++) {
            id[i][i] = 1;
        }
        return id;
    }

    public static String format(int[][] m) {
        if (m.length == 0 || m[0].length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                sb.append(m[i][j]);
                if (j < m[i].length - 1) {
                    sb.append(" ");
                }
            }
            if (i < m.length - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int[] tab = {1, 2, 3, 4};

        System.out.println(tab);
        System.out.println(java.util.Arrays.toString(tab));
    }
}
