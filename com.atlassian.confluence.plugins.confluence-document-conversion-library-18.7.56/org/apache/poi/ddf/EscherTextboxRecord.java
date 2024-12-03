/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ddf;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.EscherRecordTypes;
import org.apache.poi.ddf.EscherSerializationListener;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.RecordFormatException;

public final class EscherTextboxRecord
extends EscherRecord {
    private static int DEFAULT_MAX_RECORD_LENGTH;
    private static int MAX_RECORD_LENGTH;
    public static final short RECORD_ID;
    private static final byte[] NO_BYTES;
    private byte[] thedata = NO_BYTES;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public EscherTextboxRecord() {
    }

    public EscherTextboxRecord(EscherTextboxRecord other) {
        super(other);
        this.thedata = other.thedata == null ? NO_BYTES : (byte[])other.thedata.clone();
    }

    @Override
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, offset);
        this.thedata = IOUtils.safelyClone(data, offset + 8, bytesRemaining, MAX_RECORD_LENGTH);
        return bytesRemaining + 8;
    }

    @Override
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        int remainingBytes = this.thedata.length;
        LittleEndian.putInt(data, offset + 4, remainingBytes);
        System.arraycopy(this.thedata, 0, data, offset + 8, this.thedata.length);
        int pos = offset + 8 + this.thedata.length;
        listener.afterRecordSerialize(pos, this.getRecordId(), pos - offset, this);
        int size = pos - offset;
        if (size != this.getRecordSize()) {
            throw new RecordFormatException(size + " bytes written but getRecordSize() reports " + this.getRecordSize());
        }
        return size;
    }

    public byte[] getData() {
        return this.thedata;
    }

    public void setData(byte[] b, int start, int length) {
        this.thedata = IOUtils.safelyClone(b, start, length, MAX_RECORD_LENGTH);
    }

    public void setData(byte[] b) {
        this.setData(b, 0, b.length);
    }

    @Override
    public int getRecordSize() {
        return 8 + this.thedata.length;
    }

    @Override
    public String getRecordName() {
        return EscherRecordTypes.CLIENT_TEXTBOX.recordName;
    }

    public Enum getGenericRecordType() {
        return EscherRecordTypes.CLIENT_TEXTBOX;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "isContainer", this::isContainerRecord, "extraData", this::getData);
    }

    @Override
    public EscherTextboxRecord copy() {
        return new EscherTextboxRecord(this);
    }

    static {
        MAX_RECORD_LENGTH = DEFAULT_MAX_RECORD_LENGTH = 100000;
        RECORD_ID = EscherRecordTypes.CLIENT_TEXTBOX.typeID;
        NO_BYTES = new byte[0];
    }
}

