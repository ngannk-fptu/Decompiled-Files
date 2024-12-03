/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.NullEscherSerializationListener;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.util.LazilyConcatenatedByteArray;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Removal;

public abstract class AbstractEscherHolderRecord
extends Record {
    private static boolean DESERIALIZE;
    private final List<EscherRecord> escherRecords = new ArrayList<EscherRecord>();
    private final LazilyConcatenatedByteArray rawDataContainer = new LazilyConcatenatedByteArray();

    public AbstractEscherHolderRecord() {
    }

    public AbstractEscherHolderRecord(AbstractEscherHolderRecord other) {
        other.escherRecords.stream().map(EscherRecord::copy).forEach(this.escherRecords::add);
        this.rawDataContainer.concatenate(other.rawDataContainer);
    }

    public AbstractEscherHolderRecord(RecordInputStream in) {
        if (!DESERIALIZE) {
            this.rawDataContainer.concatenate(in.readRemainder());
        } else {
            byte[] data = in.readAllContinuedRemainder();
            this.convertToEscherRecords(0, data.length, data);
        }
    }

    @Removal(version="5.3")
    @Deprecated
    protected void convertRawBytesToEscherRecords() {
        if (!DESERIALIZE) {
            this.decode();
        }
    }

    private void convertToEscherRecords(int offset, int size, byte[] data) {
        int bytesRead;
        this.escherRecords.clear();
        DefaultEscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        for (int pos = offset; pos < offset + size; pos += bytesRead) {
            EscherRecord r = recordFactory.createRecord(data, pos);
            bytesRead = r.fillFields(data, pos, recordFactory);
            this.escherRecords.add(r);
        }
    }

    protected abstract String getRecordName();

    @Override
    public int serialize(int offset, byte[] data) {
        byte[] rawData = this.getRawData();
        LittleEndian.putShort(data, offset, this.getSid());
        LittleEndian.putShort(data, offset += 2, (short)(this.getRecordSize() - 4));
        offset += 2;
        if (this.escherRecords.isEmpty() && rawData != null) {
            System.arraycopy(rawData, 0, data, offset, rawData.length);
            return rawData.length + 4;
        }
        NullEscherSerializationListener listener = new NullEscherSerializationListener();
        for (EscherRecord r : this.escherRecords) {
            offset += r.serialize(offset, data, listener);
        }
        return this.getRecordSize();
    }

    @Override
    public int getRecordSize() {
        byte[] rawData = this.getRawData();
        if (this.escherRecords.isEmpty() && rawData != null) {
            return rawData.length;
        }
        int size = 0;
        for (EscherRecord r : this.escherRecords) {
            size += r.getRecordSize();
        }
        return size;
    }

    @Override
    public abstract short getSid();

    @Override
    public abstract AbstractEscherHolderRecord copy();

    public void addEscherRecord(int index, EscherRecord element) {
        this.escherRecords.add(index, element);
    }

    public boolean addEscherRecord(EscherRecord element) {
        return this.escherRecords.add(element);
    }

    public List<EscherRecord> getEscherRecords() {
        return this.escherRecords;
    }

    public void clearEscherRecords() {
        this.escherRecords.clear();
    }

    public EscherContainerRecord getEscherContainer() {
        for (EscherRecord er : this.escherRecords) {
            if (!(er instanceof EscherContainerRecord)) continue;
            return (EscherContainerRecord)er;
        }
        return null;
    }

    public EscherRecord findFirstWithId(short id) {
        return this.findFirstWithId(id, this.getEscherRecords());
    }

    private EscherRecord findFirstWithId(short id, List<EscherRecord> records) {
        for (EscherRecord r : records) {
            if (r.getRecordId() != id) continue;
            return r;
        }
        for (EscherRecord r : records) {
            EscherRecord found;
            if (!r.isContainerRecord() || (found = this.findFirstWithId(id, r.getChildRecords())) == null) continue;
            return found;
        }
        return null;
    }

    public EscherRecord getEscherRecord(int index) {
        return this.escherRecords.get(index);
    }

    public void join(AbstractEscherHolderRecord record) {
        this.rawDataContainer.concatenate(record.getRawData());
    }

    public void processContinueRecord(byte[] record) {
        this.rawDataContainer.concatenate(record);
    }

    public byte[] getRawData() {
        return this.rawDataContainer.toArray();
    }

    public void setRawData(byte[] rawData) {
        this.rawDataContainer.clear();
        this.rawDataContainer.concatenate(rawData);
    }

    public void decode() {
        if (this.escherRecords.isEmpty()) {
            byte[] rawData = this.getRawData();
            this.convertToEscherRecords(0, rawData.length, rawData);
        }
    }

    public List<EscherRecord> getGenericChildren() {
        return this.escherRecords;
    }

    static {
        try {
            DESERIALIZE = System.getProperty("poi.deserialize.escher") != null;
        }
        catch (SecurityException e) {
            DESERIALIZE = false;
        }
    }
}

