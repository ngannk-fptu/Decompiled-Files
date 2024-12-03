/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.common.usermodel;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface GenericRecord {
    default public Enum<?> getGenericRecordType() {
        return null;
    }

    public Map<String, Supplier<?>> getGenericProperties();

    default public List<? extends GenericRecord> getGenericChildren() {
        return null;
    }
}

