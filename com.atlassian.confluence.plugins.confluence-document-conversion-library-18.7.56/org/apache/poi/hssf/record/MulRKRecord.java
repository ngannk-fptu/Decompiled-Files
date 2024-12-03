/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.hssf.util.RKUtil;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class MulRKRecord
extends StandardRecord {
    public static final short sid = 189;
    private final int field_1_row;
    private final short field_2_first_col;
    private final RkRec[] field_3_rks;
    private final short field_4_last_col;

    public int getRow() {
        return this.field_1_row;
    }

    public short getFirstColumn() {
        return this.field_2_first_col;
    }

    public short getLastColumn() {
        return this.field_4_last_col;
    }

    public int getNumColumns() {
        return this.field_4_last_col - this.field_2_first_col + 1;
    }

    public short getXFAt(int coffset) {
        return this.field_3_rks[coffset].xf;
    }

    public double getRKNumberAt(int coffset) {
        return RKUtil.decodeNumber(this.field_3_rks[coffset].rk);
    }

    public MulRKRecord(RecordInputStream in) {
        this.field_1_row = in.readUShort();
        this.field_2_first_col = in.readShort();
        this.field_3_rks = RkRec.parseRKs(in);
        this.field_4_last_col = in.readShort();
    }

    @Override
    public short getSid() {
        return 189;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }

    @Override
    protected int getDataSize() {
        throw new RecordFormatException("Sorry, you can't serialize MulRK in this release");
    }

    @Override
    public MulRKRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.MUL_RK;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "firstColumn", this::getFirstColumn, "lastColumn", this::getLastColumn, "rk", () -> this.field_3_rks);
    }

    private static final class RkRec
    implements GenericRecord {
        public static final int ENCODED_SIZE = 6;
        public final short xf;
        public final int rk;

        private RkRec(RecordInputStream in) {
            this.xf = in.readShort();
            this.rk = in.readInt();
        }

        public static RkRec[] parseRKs(RecordInputStream in) {
            int nItems = (in.remaining() - 2) / 6;
            RkRec[] retval = new RkRec[nItems];
            for (int i = 0; i < nItems; ++i) {
                retval[i] = new RkRec(in);
            }
            return retval;
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("xf", () -> this.xf, "rk", () -> this.rk);
        }
    }
}

