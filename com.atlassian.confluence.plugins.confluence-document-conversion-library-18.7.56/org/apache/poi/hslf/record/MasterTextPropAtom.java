/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.model.textproperties.IndentProp;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class MasterTextPropAtom
extends RecordAtom {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000;
    private static int MAX_RECORD_LENGTH = 100000;
    private byte[] _header;
    private byte[] _data;
    private List<IndentProp> indents;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public MasterTextPropAtom() {
        this._header = new byte[8];
        this._data = new byte[0];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
        this.indents = new ArrayList<IndentProp>();
    }

    protected MasterTextPropAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, MAX_RECORD_LENGTH);
        try {
            this.read();
        }
        catch (Exception e) {
            LOG.atError().withThrowable(e).log("Failed to parse MasterTextPropAtom");
        }
    }

    @Override
    public long getRecordType() {
        return RecordTypes.MasterTextPropAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.write();
        out.write(this._header);
        out.write(this._data);
    }

    private void write() {
        int pos = 0;
        long newSize = Math.multiplyExact((long)this.indents.size(), 6L);
        this._data = IOUtils.safelyAllocate(newSize, MAX_RECORD_LENGTH);
        for (IndentProp prop : this.indents) {
            LittleEndian.putInt(this._data, pos, prop.getCharactersCovered());
            LittleEndian.putShort(this._data, pos + 4, (short)prop.getIndentLevel());
            pos += 6;
        }
    }

    private void read() {
        this.indents = new ArrayList<IndentProp>(this._data.length / 6);
        for (int pos = 0; pos <= this._data.length - 6; pos += 6) {
            int count = LittleEndian.getInt(this._data, pos);
            short indent = LittleEndian.getShort(this._data, pos + 4);
            this.indents.add(new IndentProp(count, indent));
        }
    }

    public int getIndentAt(int offset) {
        int charsUntil = 0;
        for (IndentProp prop : this.indents) {
            if (offset >= (charsUntil += prop.getCharactersCovered())) continue;
            return prop.getIndentLevel();
        }
        return -1;
    }

    public List<IndentProp> getIndents() {
        return Collections.unmodifiableList(this.indents);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("indents", this::getIndents);
    }
}

