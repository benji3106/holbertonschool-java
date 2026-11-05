public class CommercialSpace extends Property {
    private static final int COMMON_CHARGES_FACTOR = 2;
    private static final int INSURED_VALUE_PER_SQM = 2000;
    private static final int PREMIUM_FACTOR = 2;

    private final String activity;

    public CommercialSpace(String reference, Address address, int surface, int monthlyRent,
            EnergyClass energyClass, String activity) {
        super(reference, address, surface, monthlyRent, energyClass);
        this.activity = checkActivity(activity);
    }

    private static String checkActivity(String activity) {
        if (activity == null || activity.isBlank()) {
            throw new IllegalArgumentException("Activity must not be blank");
        }
        return activity;
    }

    public String getActivity() {
        return this.activity;
    }

    @Override
    public String getType() {
        return "Commercial space";
    }

    @Override
    public int getMonthlyCharges() {
        return this.getCommonCharges() * COMMON_CHARGES_FACTOR;
    }

    @Override
    public int getInsuredValue() {
        return this.getSurface() * INSURED_VALUE_PER_SQM;
    }

    @Override
    public int getYearlyInsurancePremium() {
        return super.getYearlyInsurancePremium() * PREMIUM_FACTOR;
    }

    @Override
    public String toString() {
        return super.toString() + " | activity: " + this.activity;
    }
}