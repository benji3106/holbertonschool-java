import java.util.Objects;

   public abstract class Property implements Insurable, Comparable<Property>, Identifiable {
    private static final int CHARGES_PER_SQM = 2;

    private final String reference;
    private Address address;
    private int surface;
    private int monthlyRent;
    private EnergyClass energyClass;
    private Owner owner;
    private Tenant tenant;

    protected Property(String reference, Address address, int surface, int monthlyRent,
            EnergyClass energyClass) {
        this.reference = checkReference(reference);
        this.address = checkAddress(address);
        this.surface = checkSurface(surface);
        this.monthlyRent = checkMonthlyRent(monthlyRent);
        this.energyClass = checkEnergyClass(energyClass);
    }

    private static String checkReference(String reference) {
        if (reference == null || !reference.matches("P-\\d{3}")) {
            throw new IllegalArgumentException("Invalid reference: " + reference);
        }
        return reference;
    }

    private static Address checkAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address must not be null");
        }
        return address;
    }

    private static int checkSurface(int surface) {
        if (surface <= 0) {
            throw new IllegalArgumentException("Surface must be positive: " + surface);
        }
        return surface;
    }

    private static int checkMonthlyRent(int monthlyRent) {
        if (monthlyRent < 0) {
            throw new IllegalArgumentException("Rent must not be negative: " + monthlyRent);
        }
        return monthlyRent;
    }

    private static EnergyClass checkEnergyClass(EnergyClass energyClass) {
        if (energyClass == null) {
            throw new IllegalArgumentException("Energy class must not be null");
        }
        return energyClass;
    }

    private static Owner checkOwner(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner must not be null");
        }
        return owner;
    }

    public abstract String getType();

    public abstract int getMonthlyCharges();

    protected int getCommonCharges() {
        return this.surface * CHARGES_PER_SQM;
    }

    public final int getMonthlyCost() {
        return this.monthlyRent + this.getMonthlyCharges();
    }

    public String getReference() {
        return this.reference;
    }

    public Address getAddress() {
        return this.address;
    }

    public int getSurface() {
        return this.surface;
    }

    public int getMonthlyRent() {
        return this.monthlyRent;
    }

    public EnergyClass getEnergyClass() {
        return this.energyClass;
    }

    public int getAnnualRent() {
        return this.monthlyRent * 12;
    }

    public boolean isRentable() {
        return this.energyClass.isRentable();
    }

    public Owner getOwner() {
        return this.owner;
    }

    public Tenant getTenant() {
        return this.tenant;
    }

    public boolean isRented() {
        return this.tenant != null;
    }

    public void setAddress(Address address) {
        this.address = checkAddress(address);
    }

    public void setSurface(int surface) {
        this.surface = checkSurface(surface);
    }

    public void setMonthlyRent(int monthlyRent) {
        this.monthlyRent = checkMonthlyRent(monthlyRent);
    }

    public void setEnergyClass(EnergyClass energyClass) {
        this.energyClass = checkEnergyClass(energyClass);
    }

    public void setOwner(Owner owner) {
        this.owner = checkOwner(owner);
    }

    public void rentTo(Tenant tenant) {
    if (tenant == null) {
        throw new IllegalArgumentException("Tenant must not be null");
    }
    if (this.isRented()) {
        throw new PropertyUnavailableException(this.reference, "already rented");
    }
    if (!this.isRentable()) {
        throw new PropertyUnavailableException(this.reference,
                "energy class " + this.energyClass);
    }
        this.tenant = tenant;
    }

    public void release() {
        if (!this.isRented()) {
            throw new IllegalStateException(this.reference + " is not rented");
        }
        this.tenant = null;
    }

    @Override
    public String getId() {
        return this.reference;
   }

    @Override
    public int compareTo(Property other) {
        return this.reference.compareTo(other.reference);
    }

    @Override
    public String toString() {
        return this.getType() + " " + this.reference + " | " + this.surface + " m2 | "
                + this.monthlyRent + " EUR/month | " + this.address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Property)) {
            return false;
        }
        Property other = (Property) o;
        return this.reference.equals(other.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reference);
    }
}