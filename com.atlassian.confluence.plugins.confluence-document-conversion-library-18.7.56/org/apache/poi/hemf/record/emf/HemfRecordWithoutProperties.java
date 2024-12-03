/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hemf.record.emf;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hemf.record.emf.HemfRecord;

interface HemfRecordWithoutProperties
extends HemfRecord {
    @Override
    default public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

