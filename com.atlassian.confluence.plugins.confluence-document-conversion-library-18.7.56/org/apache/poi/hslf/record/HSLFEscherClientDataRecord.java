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
import java.util.List;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.EscherSerializationListener;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class HSLFEscherClientDataRecord
extends EscherClientDataRecord {
    private final List<Record> _childRecords = new ArrayList<Record>();

    public HSLFEscherClientDataRecord() {
    }

    public HSLFEscherClientDataRecord(HSLFEscherClientDataRecord other) {
        super(other);
        this._childRecords.addAll(other._childRecords);
    }

    public List<? extends Record> getHSLFChildRecords() {
        return this._childRecords;
    }

    public void removeChild(Class<? extends Record> childClass) {
        this._childRecords.removeIf(childClass::isInstance);
    }

    public void addChild(Record childRecord) {
        this._childRecords.add(childRecord);
    }

    @Override
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, offset);
        byte[] remainingData = IOUtils.safelyClone(data, offset + 8, bytesRemaining, RecordAtom.getMaxRecordLength());
        this.setRemainingData(remainingData);
        return bytesRemaining + 8;
    }

    @Override
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        byte[] childBytes = this.getRemainingData();
        LittleEndian.putInt(data, offset + 4, childBytes.length);
        System.arraycopy(childBytes, 0, data, offset + 8, childBytes.length);
        int recordSize = 8 + childBytes.length;
        listener.afterRecordSerialize(offset + recordSize, this.getRecordId(), recordSize, this);
        return recordSize;
    }

    @Override
    public int getRecordSize() {
        return 8 + this.getRemainingData().length;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public byte[] getRemainingData() {
        try (UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();){
            for (Record r : this._childRecords) {
                r.writeOut((OutputStream)bos);
            }
            Object object = bos.toByteArray();
            return object;
        }
        catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    @Override
    public void setRemainingData(byte[] remainingData) {
        this._childRecords.clear();
        int offset = 0;
        while (offset < remainingData.length) {
            Record r = Record.buildRecordAtOffset(remainingData, offset);
            if (r != null) {
                this._childRecords.add(r);
            }
            long rlen = LittleEndian.getUInt(remainingData, offset + 4);
            offset = Math.toIntExact((long)(offset + 8) + rlen);
        }
    }

    @Override
    public String getRecordName() {
        return "HSLFClientData";
    }

    @Override
    public HSLFEscherClientDataRecord copy() {
        return new HSLFEscherClientDataRecord(this);
    }
}

