public class Property {
    private String reference;
    private String address;
    private int surface;
    private int monthlyRent;

    public Property(String reference, String address, int surface, int monthlyRent) {
        this.reference = reference;
        this.address = address;
        this.surface = surface;
        this.monthlyRent = monthlyRent;
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
}