public enum EnergyClass {
    A(70),
    B(110),
    C(180),
    D(250),
    E(330),
    F(420),
    G(Integer.MAX_VALUE);

    private final int maxConsumption;

    EnergyClass(int maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public int getMaxConsumption() {
        return this.maxConsumption;
    }

    public boolean isRentable() {
        return this != G;
    }

    public boolean isBetterThan(EnergyClass other) {
        return this.compareTo(other) < 0;
    }

    public static EnergyClass fromConsumption(int consumption) {
        if (consumption < 0) {
            throw new IllegalArgumentException("Consumption must not be negative: " + consumption);
        }
        for (EnergyClass energyClass : values()) {
            if (consumption <= energyClass.maxConsumption) {
                return energyClass;
            }
        }
        throw new AssertionError("Unreachable: G accepts every consumption");
    }
}