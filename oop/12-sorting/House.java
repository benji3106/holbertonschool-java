public class House extends Property {
    private static final int LAND_SURFACE_PER_EURO = 20;
    private static final int INSURED_VALUE_PER_SQM = 2500;
    private static final int INSURED_VALUE_PER_LAND_SQM = 50;

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
    public String getType() {
        return "House";
    }

    @Override
    public int getMonthlyCharges() {
        return this.getCommonCharges() + this.landSurface / LAND_SURFACE_PER_EURO;
    }

    @Override
    public int getInsuredValue() {
        return this.getSurface() * INSURED_VALUE_PER_SQM
                + this.landSurface * INSURED_VALUE_PER_LAND_SQM;
    }

    @Override
    public String toString() {
        return super.toString() + " | land " + this.landSurface + " m2";
    }
}