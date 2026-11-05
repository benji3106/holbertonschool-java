public record Lease(Property property, Tenant tenant, int durationMonths) {
    private static final int DEPOSIT_MONTHS = 2;

    public Lease {
        if (property == null) {
            throw new IllegalArgumentException("Property must not be null");
        }
        if (tenant == null) {
            throw new IllegalArgumentException("Tenant must not be null");
        }
    }

    public int totalRent() {
        return property.getMonthlyRent() * durationMonths;
    }

    public int deposit() {
        return property.getMonthlyRent() * DEPOSIT_MONTHS;
    }
}