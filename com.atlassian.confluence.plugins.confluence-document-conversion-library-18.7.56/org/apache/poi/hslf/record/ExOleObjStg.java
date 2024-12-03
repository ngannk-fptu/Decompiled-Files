/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.BoundedInputStream
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hslf.record.PersistRecord;
import org.apache.poi.hslf.record.PositionDependentRecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class ExOleObjStg
extends PositionDependentRecordAtom
implements PersistRecord {
    private static final int MAX_RECORD_LENGTH = 100000000;
    private int _persistId;
    private final byte[] _header;
    private byte[] _data;

    public ExOleObjStg() {
        this._header = new byte[8];
        this._data = new byte[0];
        LittleEndian.putShort(this._header, 0, (short)16);
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected ExOleObjStg(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, 100000000);
    }

    public boolean isCompressed() {
        return LittleEndian.getShort(this._header, 0) != 0;
    }

    public int getDataLength() {
        if (this.isCompressed()) {
            return LittleEndian.getInt(this._data, 0);
        }
        return this._data.length;
    }

    public InputStream getData() {
        if (this.isCompressed()) {
            int size = LittleEndian.getInt(this._data);
            ByteArrayInputStream compressedStream = new ByteArrayInputStream(this._data, 4, this._data.length);
            return new BoundedInputStream((InputStream)new InflaterInputStream(compressedStream), (long)size);
        }
        return new ByteArrayInputStream(this._data, 0, this._data.length);
    }

    public byte[] getRawData() {
        return this._data;
    }

    public void setData(byte[] data) throws IOException {
        try (UnsynchronizedByteArrayOutputStream out = new UnsynchronizedByteArrayOutputStream();
             DeflaterOutputStream def = new DeflaterOutputStream((OutputStream)out);){
            byte[] b = new byte[4];
            LittleEndian.putInt(b, 0, data.length);
            out.write(b);
            def.write(data, 0, data.length);
            def.finish();
            this._data = out.toByteArray();
            LittleEndian.putInt(this._header, 4, this._data.length);
        }
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExOleObjStg.typeID;
    }

    public int getRecordInstance() {
        return LittleEndian.getUShort(this._header, 0) >>> 4;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    @Override
    public int getPersistId() {
        return this._persistId;
    }

    @Override
    public void setPersistId(int id) {
        this._persistId = id;
    }

    @Override
    public void updateOtherRecordReferences(Map<Integer, Integer> oldToNewReferencesLookup) {
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("compressed", this::isCompressed, "persistId", this::getPersistId, "dataLength", this::getDataLength, "data", this::getData);
    }
}

