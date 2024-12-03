/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class BoundSheetRecord
extends StandardRecord {
    public static final short sid = 133;
    private static final BitField hiddenFlag = BitFieldFactory.getInstance(1);
    private static final BitField veryHiddenFlag = BitFieldFactory.getInstance(2);
    private int field_1_position_of_BOF;
    private int field_2_option_flags;
    private int field_4_isMultibyteUnicode;
    private String field_5_sheetname;

    public BoundSheetRecord(String sheetname) {
        this.field_2_option_flags = 0;
        this.setSheetname(sheetname);
    }

    public BoundSheetRecord(BoundSheetRecord other) {
        super(other);
        this.field_1_position_of_BOF = other.field_1_position_of_BOF;
        this.field_2_option_flags = other.field_2_option_flags;
        this.field_4_isMultibyteUnicode = other.field_4_isMultibyteUnicode;
        this.field_5_sheetname = other.field_5_sheetname;
    }

    public BoundSheetRecord(RecordInputStream in) {
        byte[] buf = new byte[4];
        in.readPlain(buf, 0, buf.length);
        this.field_1_position_of_BOF = LittleEndian.getInt(buf);
        this.field_2_option_flags = in.readUShort();
        int field_3_sheetname_length = in.readUByte();
        this.field_4_isMultibyteUnicode = in.readByte();
        this.field_5_sheetname = this.isMultibyte() ? in.readUnicodeLEString(field_3_sheetname_length) : in.readCompressedUnicode(field_3_sheetname_length);
    }

    public void setPositionOfBof(int pos) {
        this.field_1_position_of_BOF = pos;
    }

    public void setSheetname(String sheetName) {
        WorkbookUtil.validateSheetName(sheetName);
        this.field_5_sheetname = sheetName;
        this.field_4_isMultibyteUnicode = StringUtil.hasMultibyte(sheetName) ? 1 : 0;
    }

    public int getPositionOfBof() {
        return this.field_1_position_of_BOF;
    }

    private boolean isMultibyte() {
        return (this.field_4_isMultibyteUnicode & 1) != 0;
    }

    public String getSheetname() {
        return this.field_5_sheetname;
    }

    @Override
    protected int getDataSize() {
        return 8 + this.field_5_sheetname.length() * (this.isMultibyte() ? 2 : 1);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.getPositionOfBof());
        out.writeShort(this.field_2_option_flags);
        String name = this.field_5_sheetname;
        out.writeByte(name.length());
        out.writeByte(this.field_4_isMultibyteUnicode);
        if (this.isMultibyte()) {
            StringUtil.putUnicodeLE(name, out);
        } else {
            StringUtil.putCompressedUnicode(name, out);
        }
    }

    @Override
    public short getSid() {
        return 133;
    }

    public boolean isHidden() {
        return hiddenFlag.isSet(this.field_2_option_flags);
    }

    public void setHidden(boolean hidden) {
        this.field_2_option_flags = hiddenFlag.setBoolean(this.field_2_option_flags, hidden);
    }

    public boolean isVeryHidden() {
        return veryHiddenFlag.isSet(this.field_2_option_flags);
    }

    public void setVeryHidden(boolean veryHidden) {
        this.field_2_option_flags = veryHiddenFlag.setBoolean(this.field_2_option_flags, veryHidden);
    }

    public static BoundSheetRecord[] orderByBofPosition(List<BoundSheetRecord> boundSheetRecords) {
        BoundSheetRecord[] bsrs = new BoundSheetRecord[boundSheetRecords.size()];
        boundSheetRecords.toArray(bsrs);
        Arrays.sort(bsrs, BoundSheetRecord::compareRecords);
        return bsrs;
    }

    private static int compareRecords(BoundSheetRecord bsr1, BoundSheetRecord bsr2) {
        return bsr1.getPositionOfBof() - bsr2.getPositionOfBof();
    }

    @Override
    public BoundSheetRecord copy() {
        return new BoundSheetRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.BOUND_SHEET;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("bof", this::getPositionOfBof, "optionFlags", () -> this.field_2_option_flags, "multiByte", this::isMultibyte, "sheetName", this::getSheetname, "hidden", this::isHidden, "veryHidden", this::isVeryHidden);
    }
}

