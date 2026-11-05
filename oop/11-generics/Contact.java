public record Contact(String name, String email) {

    public Contact {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
    }

    private static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        int at = email.indexOf('@');
        return at > 0
                && at < email.length() - 1
                && at == email.lastIndexOf('@');
    }

    @Override
    public String toString() {
        return name + " <" + email + ">";
    }
}