/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.util.CollectionUtil;

public class VertexToIntegerMapping<V> {
    private final Map<V, Integer> vertexMap;
    private final List<V> indexList;

    public VertexToIntegerMapping(List<V> vertices) {
        Objects.requireNonNull(vertices, "the input collection of vertices cannot be null");
        this.vertexMap = CollectionUtil.newHashMapWithExpectedSize(vertices.size());
        this.indexList = vertices;
        for (V v : vertices) {
            if (this.vertexMap.put((Integer)v, this.vertexMap.size()) == null) continue;
            throw new IllegalArgumentException("vertices are not distinct");
        }
    }

    public VertexToIntegerMapping(Collection<V> vertices) {
        this((List<V>)new ArrayList<V>(Objects.requireNonNull(vertices, "the input collection of vertices cannot be null")));
    }

    public Map<V, Integer> getVertexMap() {
        return this.vertexMap;
    }

    public List<V> getIndexList() {
        return this.indexList;
    }
}

