/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.record.TextSpecInfoRun;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

public final class TextSpecInfoAtom
extends RecordAtom {
    private static final long _type = RecordTypes.TextSpecInfoAtom.typeID;
    private final byte[] _header;
    private byte[] _data;

    public TextSpecInfoAtom() {
        this._header = new byte[8];
        LittleEndian.putUInt(this._header, 4, _type);
        this.reset(1);
    }

    public TextSpecInfoAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, TextSpecInfoAtom.getMaxRecordLength());
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    public void setTextSize(int size) {
        LittleEndian.putInt(this._data, 0, size);
    }

    public void reset(int size) {
        TextSpecInfoRun sir = new TextSpecInfoRun(size);
        UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
        try {
            sir.writeOut((OutputStream)bos);
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
        this._data = bos.toByteArray();
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    public void setParentSize(int size) {
        assert (size > 0);
        try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
            TextSpecInfoRun[] runs = this.getTextSpecInfoRuns();
            int remaining = size;
            int idx = 0;
            for (TextSpecInfoRun run : runs) {
                int len = run.getLength();
                if (len > remaining || idx == runs.length - 1) {
                    len = remaining;
                    run.setLength(len);
                }
                remaining -= len;
                run.writeOut((OutputStream)bos);
                ++idx;
            }
            this._data = bos.toByteArray();
            LittleEndian.putInt(this._header, 4, this._data.length);
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    public int getCharactersCovered() {
        int covered = 0;
        for (TextSpecInfoRun r : this.getTextSpecInfoRuns()) {
            covered += r.getLength();
        }
        return covered;
    }

    public TextSpecInfoRun[] getTextSpecInfoRuns() {
        LittleEndianByteArrayInputStream bis = new LittleEndianByteArrayInputStream(this._data);
        ArrayList<TextSpecInfoRun> lst = new ArrayList<TextSpecInfoRun>();
        while (bis.getReadIndex() < this._data.length) {
            lst.add(new TextSpecInfoRun(bis));
        }
        return lst.toArray(new TextSpecInfoRun[0]);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("charactersCovered", this::getCharactersCovered, "textSpecInfoRuns", this::getTextSpecInfoRuns);
    }
}

