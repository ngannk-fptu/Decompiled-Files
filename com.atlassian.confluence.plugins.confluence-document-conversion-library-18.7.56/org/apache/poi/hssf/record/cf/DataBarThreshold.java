/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.cf;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.util.LittleEndianInput;

public final class DataBarThreshold
extends Threshold
implements Duplicatable {
    public DataBarThreshold() {
    }

    public DataBarThreshold(DataBarThreshold other) {
        super(other);
    }

    public DataBarThreshold(LittleEndianInput in) {
        super(in);
    }

    @Override
    public DataBarThreshold copy() {
        return new DataBarThreshold(this);
    }
}

