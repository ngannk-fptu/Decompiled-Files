/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.writefilter;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.tika.metadata.writefilter.MetadataWriteFilter;
import org.apache.tika.metadata.writefilter.MetadataWriteFilterFactory;
import org.apache.tika.metadata.writefilter.StandardWriteFilter;

public class StandardWriteFilterFactory
implements MetadataWriteFilterFactory {
    public static int DEFAULT_MAX_KEY_SIZE = 1024;
    public static int DEFAULT_MAX_FIELD_SIZE = 102400;
    public static int DEFAULT_TOTAL_ESTIMATED_BYTES = 0xA00000;
    public static int DEFAULT_MAX_VALUES_PER_FIELD = 10;
    private Set<String> includeFields = null;
    private int maxKeySize = DEFAULT_MAX_KEY_SIZE;
    private int maxFieldSize = DEFAULT_MAX_FIELD_SIZE;
    private int maxTotalEstimatedBytes = DEFAULT_TOTAL_ESTIMATED_BYTES;
    private int maxValuesPerField = DEFAULT_MAX_VALUES_PER_FIELD;
    private boolean includeEmpty = false;

    @Override
    public MetadataWriteFilter newInstance() {
        if (this.maxFieldSize < 0) {
            throw new IllegalArgumentException("maxFieldSize must be > 0");
        }
        if (this.maxValuesPerField < 1) {
            throw new IllegalArgumentException("maxValuesPerField must be > 0");
        }
        if (this.maxTotalEstimatedBytes < 0) {
            throw new IllegalArgumentException("max estimated size must be > 0");
        }
        return new StandardWriteFilter(this.maxKeySize, this.maxFieldSize, this.maxTotalEstimatedBytes, this.maxValuesPerField, this.includeFields, this.includeEmpty);
    }

    public void setIncludeFields(List<String> includeFields) {
        ConcurrentHashMap.KeySetView keys = ConcurrentHashMap.newKeySet(includeFields.size());
        keys.addAll(includeFields);
        this.includeFields = Collections.unmodifiableSet(keys);
    }

    public void setMaxTotalEstimatedBytes(int maxTotalEstimatedBytes) {
        this.maxTotalEstimatedBytes = maxTotalEstimatedBytes;
    }

    public void setMaxKeySize(int maxKeySize) {
        this.maxKeySize = maxKeySize;
    }

    public void setMaxFieldSize(int maxFieldSize) {
        this.maxFieldSize = maxFieldSize;
    }

    public void setIncludeEmpty(boolean includeEmpty) {
        this.includeEmpty = includeEmpty;
    }

    public void setMaxValuesPerField(int maxValuesPerField) {
        this.maxValuesPerField = maxValuesPerField;
    }

    public Set<String> getIncludeFields() {
        return this.includeFields;
    }

    public int getMaxKeySize() {
        return this.maxKeySize;
    }

    public int getMaxFieldSize() {
        return this.maxFieldSize;
    }

    public int getMaxTotalEstimatedBytes() {
        return this.maxTotalEstimatedBytes;
    }

    public int getMaxValuesPerField() {
        return this.maxValuesPerField;
    }

    public boolean isIncludeEmpty() {
        return this.includeEmpty;
    }

    public String toString() {
        return "StandardWriteFilterFactory{includeFields=" + this.includeFields + ", maxKeySize=" + this.maxKeySize + ", maxFieldSize=" + this.maxFieldSize + ", maxTotalEstimatedBytes=" + this.maxTotalEstimatedBytes + ", maxValuesPerField=" + this.maxValuesPerField + ", includeEmpty=" + this.includeEmpty + '}';
    }
}

