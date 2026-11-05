import java.util.Objects;

public class Property {
    private final String reference;
    private Address address;
    private int surface;
    private int monthlyRent;

    public Property(String reference, Address address, int surface, int monthlyRent) {
        this.reference = checkReference(reference);
        this.address = checkAddress(address);
        this.surface = checkSurface(surface);
        this.monthlyRent = checkMonthlyRent(monthlyRent);
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

    public int getAnnualRent() {
        return this.monthlyRent * 12;
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

    @Override
    public String toString() {
        return this.reference + " | " + this.surface + " m2 | "
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