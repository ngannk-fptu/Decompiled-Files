/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class DrawingSelectionRecord
extends StandardRecord {
    public static final short sid = 237;
    private OfficeArtRecordHeader _header;
    private int _cpsp;
    private int _dgslk;
    private int _spidFocus;
    private int[] _shapeIds;

    public DrawingSelectionRecord(RecordInputStream in) {
        this._header = new OfficeArtRecordHeader(in);
        this._cpsp = in.readInt();
        this._dgslk = in.readInt();
        this._spidFocus = in.readInt();
        int nShapes = in.available() / 4;
        int[] shapeIds = new int[nShapes];
        for (int i = 0; i < nShapes; ++i) {
            shapeIds[i] = in.readInt();
        }
        this._shapeIds = shapeIds;
    }

    @Override
    public short getSid() {
        return 237;
    }

    @Override
    protected int getDataSize() {
        return 20 + this._shapeIds.length * 4;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        this._header.serialize(out);
        out.writeInt(this._cpsp);
        out.writeInt(this._dgslk);
        out.writeInt(this._spidFocus);
        for (int shapeId : this._shapeIds) {
            out.writeInt(shapeId);
        }
    }

    @Override
    public DrawingSelectionRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DRAWING_SELECTION;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rh", () -> this._header, "cpsp", () -> this._cpsp, "dgslk", () -> this._dgslk, "spidFocus", () -> this._spidFocus, "shapeIds", () -> this._shapeIds);
    }

    private static final class OfficeArtRecordHeader
    implements GenericRecord {
        public static final int ENCODED_SIZE = 8;
        private final int _verAndInstance;
        private final int _type;
        private final int _length;

        public OfficeArtRecordHeader(LittleEndianInput in) {
            this._verAndInstance = in.readUShort();
            this._type = in.readUShort();
            this._length = in.readInt();
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._verAndInstance);
            out.writeShort(this._type);
            out.writeInt(this._length);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("verAndInstance", () -> this._verAndInstance, "type", () -> this._type, "length", () -> this._length);
        }
    }
}

