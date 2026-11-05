public class Owner implements Identifiable {
    private final Contact contact;

    public Owner(Contact contact) {
        this.contact = checkContact(contact);
    }

    private static Contact checkContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("Contact must not be null");
        }
        return contact;
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

    @Override
    public String getId() {
        return this.getEmail();
   }
}