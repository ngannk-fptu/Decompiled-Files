/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public final class CString
extends RecordAtom {
    private byte[] _header;
    private byte[] _text;

    public String getText() {
        return StringUtil.getFromUnicodeLE(this._text);
    }

    public void setText(String text) {
        this._text = new byte[text.length() * 2];
        StringUtil.putUnicodeLE(text, this._text, 0);
        LittleEndian.putInt(this._header, 4, this._text.length);
    }

    public int getOptions() {
        return LittleEndian.getShort(this._header);
    }

    public void setOptions(int count) {
        LittleEndian.putShort(this._header, 0, (short)count);
    }

    protected CString(byte[] source, int start, int len) {
        if (len < 8) {
            len = 8;
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._text = IOUtils.safelyClone(source, start + 8, len - 8, CString.getMaxRecordLength());
    }

    public CString() {
        this._header = new byte[]{0, 0, -70, 15, 0, 0, 0, 0};
        this._text = new byte[0];
    }

    @Override
    public long getRecordType() {
        return RecordTypes.CString.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._text);
    }

    public String toString() {
        return this.getText();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("text", this::getText);
    }
}

