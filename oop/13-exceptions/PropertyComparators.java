import java.util.Comparator;

public final class PropertyComparators {
    public static final Comparator<Property> BY_RENT =
            Comparator.comparingInt(Property::getMonthlyRent);

    public static final Comparator<Property> BY_SURFACE_DESC =
            Comparator.comparingInt(Property::getSurface).reversed();

    public static final Comparator<Property> BY_ENERGY_CLASS =
            Comparator.comparing(Property::getEnergyClass);

    public static final Comparator<Property> BY_CITY_THEN_RENT =
            Comparator.comparing((Property p) -> p.getAddress().city()).thenComparing(BY_RENT);

    private PropertyComparators() {
    }
}