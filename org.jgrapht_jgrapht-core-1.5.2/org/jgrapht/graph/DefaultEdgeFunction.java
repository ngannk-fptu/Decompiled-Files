/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DefaultEdgeFunction<E, T>
implements Function<E, T>,
Serializable {
    private static final long serialVersionUID = -4247429315268336855L;
    protected final Map<E, T> map;
    protected final T defaultValue;

    public DefaultEdgeFunction(T defaultValue) {
        this(defaultValue, new HashMap());
    }

    public DefaultEdgeFunction(T defaultValue, Map<E, T> map) {
        this.defaultValue = Objects.requireNonNull(defaultValue, "Default value cannot be null");
        this.map = Objects.requireNonNull(map, "Map cannot be null");
    }

    @Override
    public T apply(E e) {
        return this.map.getOrDefault(e, this.defaultValue);
    }

    public T get(E e) {
        return this.map.getOrDefault(e, this.defaultValue);
    }

    public void set(E e, T value) {
        this.map.put(e, value);
    }
}

