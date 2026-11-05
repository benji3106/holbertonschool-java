public record Address(String street, String zipCode, String city) {

    public Address {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street must not be blank");
        }
        if (zipCode == null || !zipCode.matches("\\d{5}")) {
            throw new IllegalArgumentException("Invalid zip code: " + zipCode);
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City must not be blank");
        }
    }

    @Override
    public String toString() {
        return street + ", " + zipCode + " " + city;
    }
}