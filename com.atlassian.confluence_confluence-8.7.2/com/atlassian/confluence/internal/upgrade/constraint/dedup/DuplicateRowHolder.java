/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.upgrade.constraint.dedup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DuplicateRowHolder {
    private final Object id;
    private final Map<String, Object> values = new HashMap<String, Object>();

    DuplicateRowHolder(Object id, Map<String, Object> values) {
        this.id = Objects.requireNonNull(id);
        this.values.putAll(values);
    }

    public boolean duplicates(Object that) {
        if (this == that) {
            return true;
        }
        if (!(that instanceof DuplicateRowHolder)) {
            return false;
        }
        DuplicateRowHolder thatDuplicateRowHolder = (DuplicateRowHolder)that;
        return this.values.keySet().stream().allMatch(column -> Objects.equals(this.values.get(column), thatDuplicateRowHolder.values.get(column)));
    }

    public Object getId() {
        return this.id;
    }
}

