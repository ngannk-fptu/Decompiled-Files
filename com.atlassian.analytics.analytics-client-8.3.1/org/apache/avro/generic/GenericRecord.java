/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.generic;

import org.apache.avro.generic.IndexedRecord;

public interface GenericRecord
extends IndexedRecord {
    public void put(String var1, Object var2);

    public Object get(String var1);

    default public boolean hasField(String key) {
        return this.getSchema().getField(key) != null;
    }
}

