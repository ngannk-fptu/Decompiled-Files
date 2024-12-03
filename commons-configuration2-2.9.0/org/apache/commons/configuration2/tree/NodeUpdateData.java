/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.configuration2.tree.QueryResult;

public class NodeUpdateData<T> {
    private final Map<QueryResult<T>, Object> changedValues;
    private final Collection<Object> newValues;
    private final Collection<QueryResult<T>> removedNodes;
    private final String key;

    public NodeUpdateData(Map<QueryResult<T>, Object> changedValues, Collection<Object> newValues, Collection<QueryResult<T>> removedNodes, String key) {
        this.changedValues = NodeUpdateData.copyMap(changedValues);
        this.newValues = NodeUpdateData.copyCollection(newValues);
        this.removedNodes = NodeUpdateData.copyCollection(removedNodes);
        this.key = key;
    }

    public Map<QueryResult<T>, Object> getChangedValues() {
        return this.changedValues;
    }

    public Collection<Object> getNewValues() {
        return this.newValues;
    }

    public Collection<QueryResult<T>> getRemovedNodes() {
        return this.removedNodes;
    }

    public String getKey() {
        return this.key;
    }

    private static <K, V> Map<K, V> copyMap(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<K, V>(map));
    }

    private static <T> Collection<T> copyCollection(Collection<? extends T> col) {
        if (col == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(new ArrayList<T>(col));
    }
}

