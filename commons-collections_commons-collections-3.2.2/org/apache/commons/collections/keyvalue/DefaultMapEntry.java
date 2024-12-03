/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.keyvalue;

import java.util.Map;
import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.AbstractMapEntry;

public final class DefaultMapEntry
extends AbstractMapEntry {
    public DefaultMapEntry(Object key, Object value) {
        super(key, value);
    }

    public DefaultMapEntry(KeyValue pair) {
        super(pair.getKey(), pair.getValue());
    }

    public DefaultMapEntry(Map.Entry entry) {
        super(entry.getKey(), entry.getValue());
    }
}

