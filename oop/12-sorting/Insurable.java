public interface Insurable {
    int MIN_PREMIUM = 150;
    int PREMIUM_PER_THOUSAND = 3;
    int THOUSAND = 1000;

    int getInsuredValue();

    default int getYearlyInsurancePremium() {
        int premium = this.getInsuredValue() * PREMIUM_PER_THOUSAND / THOUSAND;
        return Math.max(premium, MIN_PREMIUM);
    }
}