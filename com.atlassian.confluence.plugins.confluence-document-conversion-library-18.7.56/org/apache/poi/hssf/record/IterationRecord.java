/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class IterationRecord
extends StandardRecord {
    public static final short sid = 17;
    private static final BitField iterationOn = BitFieldFactory.getInstance(1);
    private int _flags;

    public IterationRecord(IterationRecord other) {
        super(other);
        this._flags = other._flags;
    }

    public IterationRecord(boolean iterateOn) {
        this._flags = iterationOn.setBoolean(0, iterateOn);
    }

    public IterationRecord(RecordInputStream in) {
        this._flags = in.readShort();
    }

    public void setIteration(boolean iterate) {
        this._flags = iterationOn.setBoolean(this._flags, iterate);
    }

    public boolean getIteration() {
        return iterationOn.isSet(this._flags);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._flags);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 17;
    }

    @Override
    public IterationRecord copy() {
        return new IterationRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.ITERATION;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("flags", () -> this._flags, "iteration", this::getIteration);
    }
}

