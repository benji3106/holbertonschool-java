import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Portfolio {
    private final List<Property> properties = new ArrayList<>();

    public boolean add(Property property) {
        if (property == null) {
            throw new IllegalArgumentException("Property must not be null");
        }
        if (this.properties.contains(property)) {
            return false;
        }
        this.properties.add(property);
        return true;
    }

    public boolean remove(String reference) {
        Property property = this.findByReference(reference);
        if (property == null) {
            return false;
        }
        return this.properties.remove(property);
    }

    public Property findByReference(String reference) {
        for (Property property : this.properties) {
            if (property.getReference().equals(reference)) {
                return property;
            }
        }
        return null;
    }

    public int size() {
        return this.properties.size();
    }

    public List<Property> getProperties() {
        return new ArrayList<>(this.properties);
    }

    public List<Property> getVacant() {
        List<Property> vacant = new ArrayList<>();
        for (Property property : this.properties) {
            if (!property.isRented()) {
                vacant.add(property);
            }
        }
        return vacant;
    }

    public List<Property> findByCity(String city) {
        List<Property> found = new ArrayList<>();
        for (Property property : this.properties) {
            if (property.getAddress().city().equalsIgnoreCase(city)) {
                found.add(property);
            }
        }
        return found;
    }

    public int getTotalMonthlyRent() {
        int total = 0;
        for (Property property : this.properties) {
            total += property.getMonthlyRent();
        }
        return total;
    }

    public int getMonthlyIncome() {
        int income = 0;
        for (Property property : this.properties) {
            if (property.isRented()) {
                income += property.getMonthlyRent();
            }
        }
        return income;
    }

    public int getOccupancyRate() {
        if (this.properties.isEmpty()) {
            return 0;
        }
        int rented = 0;
        for (Property property : this.properties) {
            if (property.isRented()) {
                rented++;
            }
        }
        return rented * 100 / this.properties.size();
    }

    public List<Property> sorted(Comparator<Property> comparator) {
        List<Property> copy = new ArrayList<>(this.properties);
        copy.sort(comparator);
        return copy;
    }

    public Property getMostExpensive() {
        Property best = null;
        for (Property property : this.properties) {
            if (best == null || property.getMonthlyRent() > best.getMonthlyRent()) {
                best = property;
            }
        }
        return best;
    }
}