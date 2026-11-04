public class ByValue {

    public static void doubleIt(int x) {
        x = x * 2;
    }

    public static void fillWithZeros(int[] t) {
        if (t != null) {
            for (int i = 0; i < t.length; i++) {
                t[i] = 0;
            }
        }
    }

    public static void swapInArray(int[] t, int i, int j) {
        if (t != null && i >= 0 && i < t.length && j >= 0 && j < t.length) {
            int temp = t[i];
            t[i] = t[j];
            t[j] = temp;
        }
    }

    public static void replaceArray(int[] t) {
        t = new int[]{9, 9, 9};
    }

    public static int demoPrimitive() {
        int n = 10;
        doubleIt(n);
        return n;
    }

    public static int[] demoArrayContent() {
        int[] arr = {1, 2, 3};
        fillWithZeros(arr);
        return arr;
    }

    public static int[] demoArrayReassign() {
        int[] arr = {1, 2, 3};
        replaceArray(arr);
        return arr;
    }

    public static void main(String[] args) {
        System.out.println("demoPrimitive() returned: " + demoPrimitive());

        int[] resContent = demoArrayContent();
        System.out.print("demoArrayContent() returned: [");
        for (int i = 0; i < resContent.length; i++) {
            System.out.print(resContent[i] + (i < resContent.length - 1 ? ", " : ""));
        }
        System.out.println("]");

        int[] resReassign = demoArrayReassign();
        System.out.print("demoArrayReassign() returned: [");
        for (int i = 0; i < resReassign.length; i++) {
            System.out.print(resReassign[i] + (i < resReassign.length - 1 ? ", " : ""));
        }
        System.out.println("]");
    }
}
