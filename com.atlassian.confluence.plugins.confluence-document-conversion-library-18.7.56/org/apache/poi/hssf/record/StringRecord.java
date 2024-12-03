/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.cont.ContinuableRecord;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.StringUtil;

public final class StringRecord
extends ContinuableRecord {
    public static final short sid = 519;
    private boolean _is16bitUnicode;
    private String _text;

    public StringRecord() {
    }

    public StringRecord(StringRecord other) {
        this._is16bitUnicode = other._is16bitUnicode;
        this._text = other._text;
    }

    public StringRecord(RecordInputStream in) {
        int field_1_string_length = in.readUShort();
        this._is16bitUnicode = in.readByte() != 0;
        this._text = this._is16bitUnicode ? in.readUnicodeLEString(field_1_string_length) : in.readCompressedUnicode(field_1_string_length);
    }

    @Override
    protected void serialize(ContinuableRecordOutput out) {
        out.writeShort(this._text.length());
        out.writeStringData(this._text);
    }

    @Override
    public short getSid() {
        return 519;
    }

    public String getString() {
        return this._text;
    }

    public void setString(String string) {
        this._text = string;
        this._is16bitUnicode = StringUtil.hasMultibyte(string);
    }

    @Override
    public StringRecord copy() {
        return new StringRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.STRING;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("is16bitUnicode", () -> this._is16bitUnicode, "text", this::getString);
    }
}

