import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Registry<T extends Identifiable> {
    private final Map<String, T> items = new TreeMap<>();

    public void register(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        String id = item.getId();
        if (this.items.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate id: " + id);
        }
        this.items.put(id, item);
    }

    public T get(String id) {
        return this.items.get(id);
    }

    public boolean contains(String id) {
        return this.items.containsKey(id);
    }

    public T unregister(String id) {
        return this.items.remove(id);
    }

    public int size() {
        return this.items.size();
    }

    public List<T> getAll() {
        return new ArrayList<>(this.items.values());
    }
}