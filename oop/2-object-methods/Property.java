import java.util.Objects;
public class Property {
    private final String reference;
    private String address;
    private int surface;
    private int monthlyRent;

    public Property(String reference, String address, int surface, int monthlyRent) {
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

    private static String checkAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address must not be blank");
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

    public String getAddress() {
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

    public void setAddress(String address) {
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