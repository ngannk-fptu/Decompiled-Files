/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.model.textproperties.TextPFException9;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class StyleTextProp9Atom
extends RecordAtom {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000;
    private static int MAX_RECORD_LENGTH = 100000;
    private final TextPFException9[] autoNumberSchemes;
    private byte[] header;
    private byte[] data;
    private short version;
    private short recordId;
    private int length;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected StyleTextProp9Atom(byte[] source, int start, int len) {
        LinkedList<TextPFException9> schemes = new LinkedList<TextPFException9>();
        this.header = Arrays.copyOfRange(source, start, start + 8);
        this.version = LittleEndian.getShort(this.header, 0);
        this.recordId = LittleEndian.getShort(this.header, 2);
        this.length = LittleEndian.getInt(this.header, 4);
        this.data = IOUtils.safelyClone(source, start + 8, len - 8, MAX_RECORD_LENGTH);
        int i = 0;
        while (i < this.data.length) {
            TextPFException9 item = new TextPFException9(this.data, i);
            schemes.add(item);
            if ((i += item.getRecordLength()) + 4 >= this.data.length) break;
            int textCfException9 = LittleEndian.getInt(this.data, i);
            if ((i += 4) + 4 >= this.data.length) break;
            int textSiException = LittleEndian.getInt(this.data, i);
            i += 4;
            if (0 != (textSiException & 0x40)) {
                i += 2;
            }
            if (i + 4 < this.data.length) continue;
            break;
        }
        this.autoNumberSchemes = schemes.toArray(new TextPFException9[0]);
    }

    @Override
    public long getRecordType() {
        return this.recordId;
    }

    public short getVersion() {
        return this.version;
    }

    public int getLength() {
        return this.length;
    }

    public TextPFException9[] getAutoNumberTypes() {
        return this.autoNumberSchemes;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this.header);
        out.write(this.data);
    }

    public void setTextSize(int size) {
        LittleEndian.putInt(this.data, 0, size);
    }

    public void reset(int size) {
        this.data = new byte[10];
        LittleEndian.putInt(this.data, 0, size);
        LittleEndian.putInt(this.data, 4, 1);
        LittleEndian.putShort(this.data, 8, (short)0);
        LittleEndian.putInt(this.header, 4, this.data.length);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("autoNumberSchemes", this::getAutoNumberTypes);
    }
}

