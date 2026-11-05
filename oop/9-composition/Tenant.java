public class Tenant {
    private static final int INCOME_RATIO = 3;

    private final Contact contact;
    private final int monthlyIncome;

    public Tenant(Contact contact, int monthlyIncome) {
        this.contact = checkContact(contact);
        this.monthlyIncome = checkMonthlyIncome(monthlyIncome);
    }

    private static Contact checkContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact must not be null");
        }
        return contact;
    }

    private static int checkMonthlyIncome(int monthlyIncome) {
        if (monthlyIncome < 0) {
            throw new IllegalArgumentException("Income must not be negative: " + monthlyIncome);
        }
        return monthlyIncome;
    }

    public Contact getContact() {
        return this.contact;
    }

    public String getName() {
        return this.contact.name();
    }

    public String getEmail() {
        return this.contact.email();
    }

    public int getMonthlyIncome() {
        return this.monthlyIncome;
    }

    public boolean canAfford(int monthlyCost) {
        return this.monthlyIncome >= INCOME_RATIO * monthlyCost;
    }
}