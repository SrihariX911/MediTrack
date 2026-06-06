package com.airtribe.meditrack.interfaces;

import java.util.List;

/**
 * Generic search contract for entity services.
 *
 * @param <T> the entity type to search
 */
public interface Searchable<T> {

    /**
     * Search by a free-text query (matches name or ID).
     */
    List<T> search(String query);

    /**
     * Look up an entity by its exact ID.
     */
    T findById(String id);

    /** Default helper to format a single search result for display. */
    default String formatSearchResult(T item) {
        return item != null ? item.toString() : "Not found";
    }
}
