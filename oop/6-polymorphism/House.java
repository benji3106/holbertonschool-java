public class House extends Property {
    private static final int LAND_SURFACE_PER_EURO = 20;

    private final int landSurface;

    public House(String reference, Address address, int surface, int monthlyRent,
            EnergyClass energyClass, int landSurface) {
        super(reference, address, surface, monthlyRent, energyClass);
        this.landSurface = checkLandSurface(landSurface);
    }

    private static int checkLandSurface(int landSurface) {
        if (landSurface < 0) {
            throw new IllegalArgumentException("Land surface must not be negative: " + landSurface);
        }
        return landSurface;
    }

    public int getLandSurface() {
        return this.landSurface;
    }

    @Override
    public int getMonthlyCharges() {
        return super.getMonthlyCharges() + this.landSurface / LAND_SURFACE_PER_EURO;
    }

    @Override
    public String toString() {
        return "House " + super.toString() + " | land " + this.landSurface + " m2";
    }
}