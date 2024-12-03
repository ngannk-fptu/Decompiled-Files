/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IntegerIdProvider<T>
implements Function<T, String> {
    private int nextId = 1;
    private final Map<T, Integer> idMap;

    public IntegerIdProvider() {
        this(1);
    }

    public IntegerIdProvider(int nextId) {
        this.nextId = nextId;
        this.idMap = new HashMap<T, Integer>();
    }

    @Override
    public String apply(T t) {
        Integer id = this.idMap.get(t);
        if (id == null) {
            id = this.nextId++;
            this.idMap.put(t, id);
        }
        return String.valueOf(id);
    }
}

