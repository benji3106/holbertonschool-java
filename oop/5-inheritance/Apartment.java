public class Apartment extends Property {
    private final int floor;
    private final boolean hasElevator;

    public Apartment(String reference, Address address, int surface, int monthlyRent,
            EnergyClass energyClass, int floor, boolean hasElevator) {
        super(reference, address, surface, monthlyRent, energyClass);
        this.floor = checkFloor(floor);
        this.hasElevator = hasElevator;
    }

    private static int checkFloor(int floor) {
        if (floor < 0) {
            throw new IllegalArgumentException("Floor must not be negative: " + floor);
        }
        return floor;
    }

    public int getFloor() {
        return this.floor;
    }

    public boolean hasElevator() {
        return this.hasElevator;
    }
}