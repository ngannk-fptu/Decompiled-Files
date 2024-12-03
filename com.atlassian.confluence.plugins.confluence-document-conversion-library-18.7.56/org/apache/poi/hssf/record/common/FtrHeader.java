/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.common;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class FtrHeader
implements Duplicatable,
GenericRecord {
    private short recordType;
    private short grbitFrt;
    private CellRangeAddress associatedRange;

    public FtrHeader() {
        this.associatedRange = new CellRangeAddress(0, 0, 0, 0);
    }

    public FtrHeader(FtrHeader other) {
        this.recordType = other.recordType;
        this.grbitFrt = other.grbitFrt;
        this.associatedRange = other.associatedRange.copy();
    }

    public FtrHeader(RecordInputStream in) {
        this.recordType = in.readShort();
        this.grbitFrt = in.readShort();
        this.associatedRange = new CellRangeAddress(in);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.recordType);
        out.writeShort(this.grbitFrt);
        this.associatedRange.serialize(out);
    }

    public static int getDataSize() {
        return 12;
    }

    public short getRecordType() {
        return this.recordType;
    }

    public void setRecordType(short recordType) {
        this.recordType = recordType;
    }

    public short getGrbitFrt() {
        return this.grbitFrt;
    }

    public void setGrbitFrt(short grbitFrt) {
        this.grbitFrt = grbitFrt;
    }

    public CellRangeAddress getAssociatedRange() {
        return this.associatedRange;
    }

    public void setAssociatedRange(CellRangeAddress associatedRange) {
        this.associatedRange = associatedRange;
    }

    @Override
    public FtrHeader copy() {
        return new FtrHeader(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("recordType", this::getRecordType, "grbitFrt", this::getGrbitFrt, "associatedRange", this::getAssociatedRange);
    }
}

