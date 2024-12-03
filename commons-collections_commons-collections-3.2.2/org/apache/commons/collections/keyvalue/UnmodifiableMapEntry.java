/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.Unmodifiable;
import org.apache.commons.collections.keyvalue.AbstractMapEntry;

public final class UnmodifiableMapEntry
extends AbstractMapEntry
implements Unmodifiable {
    public UnmodifiableMapEntry(Object key, Object value) {
        super(key, value);
    }

    public UnmodifiableMapEntry(KeyValue pair) {
        super(pair.getKey(), pair.getValue());
    }

    public UnmodifiableMapEntry(Map.Entry entry) {
        super(entry.getKey(), entry.getValue());
    }

    public Object setValue(Object value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }
}

