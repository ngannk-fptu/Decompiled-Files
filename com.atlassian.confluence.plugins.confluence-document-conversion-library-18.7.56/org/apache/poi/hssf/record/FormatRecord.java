/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;

public final class FormatRecord
extends StandardRecord {
    private static final Logger LOG = LogManager.getLogger(FormatRecord.class);
    public static final short sid = 1054;
    private final int field_1_index_code;
    private final boolean field_3_hasMultibyte;
    private final String field_4_formatstring;

    private FormatRecord(FormatRecord other) {
        super(other);
        this.field_1_index_code = other.field_1_index_code;
        this.field_3_hasMultibyte = other.field_3_hasMultibyte;
        this.field_4_formatstring = other.field_4_formatstring;
    }

    public FormatRecord(int indexCode, String fs) {
        this.field_1_index_code = indexCode;
        this.field_4_formatstring = fs;
        this.field_3_hasMultibyte = StringUtil.hasMultibyte(fs);
    }

    public FormatRecord(RecordInputStream in) {
        this.field_1_index_code = in.readShort();
        int field_3_unicode_len = in.readUShort();
        this.field_3_hasMultibyte = (in.readByte() & 1) != 0;
        this.field_4_formatstring = this.field_3_hasMultibyte ? FormatRecord.readStringCommon(in, field_3_unicode_len, false) : FormatRecord.readStringCommon(in, field_3_unicode_len, true);
    }

    public int getIndexCode() {
        return this.field_1_index_code;
    }

    public String getFormatString() {
        return this.field_4_formatstring;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        String formatString = this.getFormatString();
        out.writeShort(this.getIndexCode());
        out.writeShort(formatString.length());
        out.writeByte(this.field_3_hasMultibyte ? 1 : 0);
        if (this.field_3_hasMultibyte) {
            StringUtil.putUnicodeLE(formatString, out);
        } else {
            StringUtil.putCompressedUnicode(formatString, out);
        }
    }

    @Override
    protected int getDataSize() {
        return 5 + this.getFormatString().length() * (this.field_3_hasMultibyte ? 2 : 1);
    }

    @Override
    public short getSid() {
        return 1054;
    }

    @Override
    public FormatRecord copy() {
        return new FormatRecord(this);
    }

    private static String readStringCommon(RecordInputStream ris, int requestedLength, boolean pIsCompressedEncoding) {
        if (requestedLength < 0 || requestedLength > 0x100000) {
            throw new IllegalArgumentException("Bad requested string length (" + requestedLength + ")");
        }
        int availableChars = pIsCompressedEncoding ? ris.remaining() : ris.remaining() / 2;
        char[] buf = requestedLength == availableChars ? new char[requestedLength] : new char[availableChars];
        for (int i = 0; i < buf.length; ++i) {
            char ch = pIsCompressedEncoding ? (char)ris.readUByte() : (char)ris.readShort();
            buf[i] = ch;
        }
        if (ris.available() == 1) {
            char[] tmp = Arrays.copyOf(buf, buf.length + 1);
            tmp[buf.length] = (char)ris.readUByte();
            buf = tmp;
        }
        if (ris.available() > 0) {
            LOG.atInfo().log("FormatRecord has {} unexplained bytes. Silently skipping", (Object)Unbox.box(ris.available()));
            while (ris.available() > 0) {
                ris.readByte();
            }
        }
        return new String(buf);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FORMAT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("indexCode", this::getIndexCode, "unicode", () -> this.field_3_hasMultibyte, "formatString", this::getFormatString);
    }
}

