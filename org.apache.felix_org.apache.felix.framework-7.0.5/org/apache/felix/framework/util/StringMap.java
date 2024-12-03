/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util;

import java.util.Map;
import java.util.TreeMap;
import org.apache.felix.framework.util.StringComparator;

public class StringMap
extends TreeMap<String, Object> {
    public StringMap() {
        super(StringComparator.COMPARATOR);
    }

    public StringMap(Map<?, ?> map) {
        this();
        for (Map.Entry<?, ?> e : map.entrySet()) {
            this.put(e.getKey().toString(), e.getValue());
        }
    }
}

