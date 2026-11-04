public class Counter {
    private int value;

    public Counter() {
        this.value = 0;
    }

    public Counter(int value) {
        this.value = value;
    }

    public void increment() {
        this.value++;
    }

    public int getValue() {
        return this.value;
    }

    void reset() {
        this.value = 0;
    }

    protected String describe() {
        return "Counter[value=" + this.value + "]";
    }
}