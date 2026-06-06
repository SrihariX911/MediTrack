package com.airtribe.meditrack.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Generic in-memory key-value store.
 * Demonstrates: generics, bounded type parameters, and iterator pattern.
 *
 * @param <T> the stored entity type
 */
public class DataStore<T> {

    private final Map<String, T> store = new HashMap<>();

    public void save(String key, T item) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key must not be null or blank");
        }
        store.put(key, item);
    }

    public Optional<T> findById(String key) {
        return Optional.ofNullable(store.get(key));
    }

    public boolean delete(String key) {
        return store.remove(key) != null;
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean exists(String key) {
        return store.containsKey(key);
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }

    /** Iterate over all entries — demonstrates Iterator usage. */
    public void forEach(java.util.function.BiConsumer<String, T> action) {
        store.forEach(action);
    }
}
