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
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

public final class TextBytesAtom
extends RecordAtom {
    public static final long _type = RecordTypes.TextBytesAtom.typeID;
    private byte[] _header;
    private byte[] _text;

    public String getText() {
        return StringUtil.getFromCompressedUnicode(this._text, 0, this._text.length);
    }

    public void setText(byte[] b) {
        this._text = (byte[])b.clone();
        LittleEndian.putInt(this._header, 4, this._text.length);
    }

    protected TextBytesAtom(byte[] source, int start, int len) {
        if (len < 8) {
            len = 8;
        }
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._text = IOUtils.safelyClone(source, start + 8, len - 8, TextBytesAtom.getMaxRecordLength());
    }

    public TextBytesAtom() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 0);
        LittleEndian.putUShort(this._header, 2, (int)_type);
        LittleEndian.putInt(this._header, 4, 0);
        this._text = new byte[0];
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._text);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("text", this::getText);
    }
}

