/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.OldStringRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.RecordFormatException;

public final class OldSheetRecord
implements GenericRecord {
    public static final short sid = 133;
    private final int field_1_position_of_BOF;
    private final int field_2_visibility;
    private final int field_3_type;
    private final byte[] field_5_sheetname;
    private CodepageRecord codepage;

    public OldSheetRecord(RecordInputStream in) {
        this.field_1_position_of_BOF = in.readInt();
        this.field_2_visibility = in.readUByte();
        this.field_3_type = in.readUByte();
        int field_4_sheetname_length = in.readUByte();
        if (field_4_sheetname_length > 0) {
            in.mark(1);
            byte b = in.readByte();
            if (b != 0) {
                try {
                    in.reset();
                }
                catch (IOException e) {
                    throw new RecordFormatException(e);
                }
            }
        }
        this.field_5_sheetname = IOUtils.safelyAllocate(field_4_sheetname_length, HSSFWorkbook.getMaxRecordLength());
        in.read(this.field_5_sheetname, 0, field_4_sheetname_length);
    }

    public void setCodePage(CodepageRecord codepage) {
        this.codepage = codepage;
    }

    public short getSid() {
        return 133;
    }

    public int getPositionOfBof() {
        return this.field_1_position_of_BOF;
    }

    public String getSheetname() {
        return OldStringRecord.getString(this.field_5_sheetname, this.codepage);
    }

    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.BOUND_SHEET;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("bof", this::getPositionOfBof, "visibility", () -> this.field_2_visibility, "type", () -> this.field_3_type, "sheetName", this::getSheetname);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }
}

