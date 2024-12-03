/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.NullEscherSerializationListener;
import org.apache.poi.hssf.record.AbstractEscherHolderRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.Removal;

public final class DrawingGroupRecord
extends AbstractEscherHolderRecord {
    public static final short sid = 235;
    private static final int DEFAULT_MAX_RECORD_SIZE = 8228;
    private static int MAX_RECORD_SIZE = 8228;

    public static void setMaxRecordSize(int size) {
        MAX_RECORD_SIZE = size;
    }

    public static int getMaxRecordSize() {
        return MAX_RECORD_SIZE;
    }

    private static int getMaxDataSize() {
        return MAX_RECORD_SIZE - 4;
    }

    public DrawingGroupRecord() {
    }

    public DrawingGroupRecord(DrawingGroupRecord other) {
        super(other);
    }

    public DrawingGroupRecord(RecordInputStream in) {
        super(in);
    }

    @Override
    protected String getRecordName() {
        return "MSODRAWINGGROUP";
    }

    @Override
    public short getSid() {
        return 235;
    }

    @Override
    public int serialize(int offset, byte[] data) {
        byte[] rawData = this.getRawData();
        if (this.getEscherRecords().isEmpty() && rawData != null) {
            return this.writeData(offset, data, rawData);
        }
        byte[] buffer = new byte[this.getRawDataSize()];
        int pos = 0;
        for (EscherRecord r : this.getEscherRecords()) {
            pos += r.serialize(pos, buffer, new NullEscherSerializationListener());
        }
        return this.writeData(offset, data, buffer);
    }

    @Removal(version="5.3")
    @Deprecated
    public void processChildRecords() {
        this.decode();
    }

    @Override
    public int getRecordSize() {
        return DrawingGroupRecord.grossSizeFromDataSize(this.getRawDataSize());
    }

    private int getRawDataSize() {
        List<EscherRecord> escherRecords = this.getEscherRecords();
        byte[] rawData = this.getRawData();
        if (escherRecords.isEmpty() && rawData != null) {
            return rawData.length;
        }
        int size = 0;
        for (EscherRecord r : escherRecords) {
            size += r.getRecordSize();
        }
        return size;
    }

    static int grossSizeFromDataSize(int dataSize) {
        return dataSize + ((dataSize - 1) / DrawingGroupRecord.getMaxDataSize() + 1) * 4;
    }

    private int writeData(int offset, byte[] data, byte[] rawData) {
        int writtenActualData = 0;
        int writtenRawData = 0;
        while (writtenRawData < rawData.length) {
            int maxDataSize = DrawingGroupRecord.getMaxDataSize();
            int segmentLength = Math.min(rawData.length - writtenRawData, maxDataSize);
            if (writtenRawData / maxDataSize >= 2) {
                this.writeContinueHeader(data, offset, segmentLength);
            } else {
                this.writeHeader(data, offset, segmentLength);
            }
            writtenActualData += 4;
            System.arraycopy(rawData, writtenRawData, data, offset += 4, segmentLength);
            offset += segmentLength;
            writtenRawData += segmentLength;
            writtenActualData += segmentLength;
        }
        return writtenActualData;
    }

    private void writeHeader(byte[] data, int offset, int sizeExcludingHeader) {
        LittleEndian.putShort(data, offset, this.getSid());
        LittleEndian.putShort(data, offset + 2, (short)sizeExcludingHeader);
    }

    private void writeContinueHeader(byte[] data, int offset, int sizeExcludingHeader) {
        LittleEndian.putShort(data, offset, (short)60);
        LittleEndian.putShort(data, offset + 2, (short)sizeExcludingHeader);
    }

    @Override
    public DrawingGroupRecord copy() {
        return new DrawingGroupRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DRAWING_GROUP;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

