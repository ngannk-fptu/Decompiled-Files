/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.OldCellRecord;
import org.apache.poi.hssf.record.OldStringRecord;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.RecordFormatException;

public final class OldLabelRecord
extends OldCellRecord {
    private static final Logger LOG = LogManager.getLogger(OldLabelRecord.class);
    public static final short biff2_sid = 4;
    public static final short biff345_sid = 516;
    private short field_4_string_len;
    private final byte[] field_5_bytes;
    private CodepageRecord codepage;

    public OldLabelRecord(RecordInputStream in) {
        super(in, in.getSid() == 4);
        this.field_4_string_len = this.isBiff2() ? (short)in.readUByte() : in.readShort();
        this.field_5_bytes = IOUtils.safelyAllocate(this.field_4_string_len, HSSFWorkbook.getMaxRecordLength());
        in.read(this.field_5_bytes, 0, this.field_4_string_len);
        if (in.remaining() > 0) {
            LOG.atInfo().log("LabelRecord data remains: {} : {}", (Object)Unbox.box(in.remaining()), (Object)HexDump.toHex(in.readRemainder()));
        }
    }

    public void setCodePage(CodepageRecord codepage) {
        this.codepage = codepage;
    }

    public short getStringLength() {
        return this.field_4_string_len;
    }

    public String getValue() {
        return OldStringRecord.getString(this.field_5_bytes, this.codepage);
    }

    public int serialize(int offset, byte[] data) {
        throw new RecordFormatException("Old Label Records are supported READ ONLY");
    }

    public int getRecordSize() {
        throw new RecordFormatException("Old Label Records are supported READ ONLY");
    }

    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.LABEL;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "stringLength", this::getStringLength, "value", this::getValue);
    }
}

