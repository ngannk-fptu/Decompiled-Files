/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ChartFRTInfoRecord
extends StandardRecord {
    public static final short sid = 2128;
    private final short rt;
    private final short grbitFrt;
    private final byte verOriginator;
    private final byte verWriter;
    private CFRTID[] rgCFRTID;

    public ChartFRTInfoRecord(ChartFRTInfoRecord other) {
        super(other);
        this.rt = other.rt;
        this.grbitFrt = other.grbitFrt;
        this.verOriginator = other.verOriginator;
        this.verWriter = other.verWriter;
        if (other.rgCFRTID != null) {
            this.rgCFRTID = (CFRTID[])Stream.of(other.rgCFRTID).map(CFRTID::new).toArray(CFRTID[]::new);
        }
    }

    public ChartFRTInfoRecord(RecordInputStream in) {
        this.rt = in.readShort();
        this.grbitFrt = in.readShort();
        this.verOriginator = in.readByte();
        this.verWriter = in.readByte();
        int cCFRTID = in.readShort();
        if (cCFRTID < 0) {
            throw new IllegalArgumentException("Had negative CFRTID: " + cCFRTID);
        }
        this.rgCFRTID = new CFRTID[cCFRTID];
        for (int i = 0; i < cCFRTID; ++i) {
            this.rgCFRTID[i] = new CFRTID(in);
        }
    }

    @Override
    protected int getDataSize() {
        return 8 + this.rgCFRTID.length * 4;
    }

    @Override
    public short getSid() {
        return 2128;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.rt);
        out.writeShort(this.grbitFrt);
        out.writeByte(this.verOriginator);
        out.writeByte(this.verWriter);
        out.writeShort(this.rgCFRTID.length);
        for (CFRTID cfrtid : this.rgCFRTID) {
            cfrtid.serialize(out);
        }
    }

    @Override
    public ChartFRTInfoRecord copy() {
        return new ChartFRTInfoRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CHART_FRT_INFO;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rt", () -> this.rt, "grbitFrt", () -> this.grbitFrt, "verOriginator", () -> this.verOriginator, "verWriter", () -> this.verWriter, "rgCFRTIDs", () -> this.rgCFRTID);
    }

    private static final class CFRTID {
        public static final int ENCODED_SIZE = 4;
        private final int rtFirst;
        private final int rtLast;

        public CFRTID(CFRTID other) {
            this.rtFirst = other.rtFirst;
            this.rtLast = other.rtLast;
        }

        public CFRTID(LittleEndianInput in) {
            this.rtFirst = in.readShort();
            this.rtLast = in.readShort();
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this.rtFirst);
            out.writeShort(this.rtLast);
        }
    }
}

