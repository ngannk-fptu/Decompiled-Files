/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 */
package com.atlassian.confluence.plugins.graphql.providers;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

class ExpandableRequest<T> {
    private final T key;
    private final List<Expansion> expansions;

    ExpandableRequest(T key, String expand) {
        this.key = key;
        this.expansions = Collections.unmodifiableList(Arrays.asList(ExpansionsParser.parse((String)expand)));
    }

    ExpandableRequest(T page, Expansion[] key) {
        this.key = page;
        this.expansions = Collections.unmodifiableList(Arrays.asList(key));
    }

    Expansion[] getExpansions() {
        return this.expansions.toArray(new Expansion[0]);
    }

    T getKey() {
        return this.key;
    }

    public static <K, V> CompletableFuture<List<V>> queryByExpansions(List<ExpandableRequest<K>> keys, Function<V, K> valueToKey, BiFunction<Expansion[], List<K>, List<V>> query) {
        HashMap resolved = new HashMap();
        for (Map.Entry entry : ExpandableRequest.groupByExpansions(keys).entrySet()) {
            List<V> values = query.apply(entry.getKey(), entry.getValue());
            values.forEach(v -> resolved.put(new ExpandableRequest(valueToKey.apply(v), (Expansion[])entry.getKey()), v));
        }
        return CompletableFuture.completedFuture(keys.stream().map(resolved::get).collect(Collectors.toList()));
    }

    private static <T> Map<Expansion[], List<T>> groupByExpansions(List<ExpandableRequest<T>> keys) {
        return keys.stream().collect(Collectors.groupingBy(ExpandableRequest::getExpansions, Collectors.mapping(ExpandableRequest::getKey, Collectors.toList())));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExpandableRequest that = (ExpandableRequest)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.expansions, that.expansions);
    }

    public int hashCode() {
        int result = Objects.hash(this.key);
        result = 31 * result + Objects.hashCode(this.expansions);
        return result;
    }
}

