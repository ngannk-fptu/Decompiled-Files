/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.EscherSerializationListener;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndian;

public class EscherPlaceholder
extends EscherRecord {
    public static final short RECORD_ID = RecordTypes.OEPlaceholderAtom.typeID;
    public static final String RECORD_DESCRIPTION = "msofbtClientTextboxPlaceholder";
    private int position = -1;
    private byte placementId;
    private byte size;
    private short unused;

    public EscherPlaceholder() {
    }

    public EscherPlaceholder(EscherPlaceholder other) {
        super(other);
        this.position = other.position;
        this.placementId = other.placementId;
        this.size = other.size;
        this.unused = other.unused;
    }

    @Override
    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = this.readHeader(data, offset);
        this.position = LittleEndian.getInt(data, offset + 8);
        this.placementId = data[offset + 12];
        this.size = data[offset + 13];
        this.unused = LittleEndian.getShort(data, offset + 14);
        assert (bytesRemaining + 8 == 16);
        return bytesRemaining + 8;
    }

    @Override
    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, this.getRecordId(), this);
        LittleEndian.putShort(data, offset, this.getOptions());
        LittleEndian.putShort(data, offset + 2, this.getRecordId());
        LittleEndian.putInt(data, offset + 4, 8);
        LittleEndian.putInt(data, offset + 8, this.position);
        LittleEndian.putByte(data, offset + 12, this.placementId);
        LittleEndian.putByte(data, offset + 13, this.size);
        LittleEndian.putShort(data, offset + 14, this.unused);
        listener.afterRecordSerialize(offset + this.getRecordSize(), this.getRecordId(), this.getRecordSize(), this);
        return this.getRecordSize();
    }

    @Override
    public int getRecordSize() {
        return 16;
    }

    @Override
    public String getRecordName() {
        return "ClientTextboxPlaceholder";
    }

    public int getPosition() {
        return this.position;
    }

    public byte getPlacementId() {
        return this.placementId;
    }

    public byte getSize() {
        return this.size;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "position", this::getPosition, "placementId", this::getPlacementId, "size", this::getSize);
    }

    public Enum getGenericRecordType() {
        return RecordTypes.OEPlaceholderAtom;
    }

    @Override
    public EscherPlaceholder copy() {
        return new EscherPlaceholder(this);
    }
}

