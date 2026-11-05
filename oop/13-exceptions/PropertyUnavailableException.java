public class PropertyUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String reference;

    public PropertyUnavailableException(String reference, String reason) {
        super(reference + " is unavailable: " + reason);
        this.reference = reference;
    }

    public String getReference() {
        return this.reference;
    }
}