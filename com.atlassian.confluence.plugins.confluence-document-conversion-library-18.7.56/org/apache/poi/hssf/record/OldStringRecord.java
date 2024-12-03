/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.CodePageUtil;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;

public final class OldStringRecord
implements GenericRecord {
    public static final short biff2_sid = 7;
    public static final short biff345_sid = 519;
    private short sid;
    private short field_1_string_len;
    private byte[] field_2_bytes;
    private CodepageRecord codepage;

    public OldStringRecord(RecordInputStream in) {
        this.sid = in.getSid();
        this.field_1_string_len = in.getSid() == 7 ? (short)in.readUByte() : in.readShort();
        this.field_2_bytes = IOUtils.safelyAllocate(this.field_1_string_len, HSSFWorkbook.getMaxRecordLength());
        in.read(this.field_2_bytes, 0, this.field_1_string_len);
    }

    public boolean isBiff2() {
        return this.sid == 7;
    }

    public short getSid() {
        return this.sid;
    }

    public void setCodePage(CodepageRecord codepage) {
        this.codepage = codepage;
    }

    public String getString() {
        return OldStringRecord.getString(this.field_2_bytes, this.codepage);
    }

    protected static String getString(byte[] data, CodepageRecord codepage) {
        int cp = 1252;
        if (codepage != null) {
            cp = codepage.getCodepage() & 0xFFFF;
        }
        try {
            return CodePageUtil.getStringFromCodePage(data, cp);
        }
        catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException("Unsupported codepage requested", uee);
        }
    }

    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.STRING;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("string", this::getString);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }
}

