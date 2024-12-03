/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.pivottable;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.RecordFormatException;

public final class PageItemRecord
extends StandardRecord {
    public static final short sid = 182;
    private final FieldInfo[] _fieldInfos;

    public PageItemRecord(PageItemRecord other) {
        super(other);
        this._fieldInfos = (FieldInfo[])Stream.of(other._fieldInfos).map(FieldInfo::new).toArray(FieldInfo[]::new);
    }

    public PageItemRecord(RecordInputStream in) {
        int dataSize = in.remaining();
        if (dataSize % 6 != 0) {
            throw new RecordFormatException("Bad data size " + dataSize);
        }
        int nItems = dataSize / 6;
        FieldInfo[] fis = new FieldInfo[nItems];
        for (int i = 0; i < fis.length; ++i) {
            fis[i] = new FieldInfo(in);
        }
        this._fieldInfos = fis;
    }

    @Override
    protected void serialize(LittleEndianOutput out) {
        for (FieldInfo fieldInfo : this._fieldInfos) {
            fieldInfo.serialize(out);
        }
    }

    @Override
    protected int getDataSize() {
        return this._fieldInfos.length * 6;
    }

    @Override
    public short getSid() {
        return 182;
    }

    @Override
    public PageItemRecord copy() {
        return new PageItemRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.PAGE_ITEM;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("fieldInfos", () -> this._fieldInfos);
    }

    private static final class FieldInfo
    implements GenericRecord {
        public static final int ENCODED_SIZE = 6;
        private int _isxvi;
        private int _isxvd;
        private int _idObj;

        public FieldInfo(FieldInfo other) {
            this._isxvi = other._isxvi;
            this._isxvd = other._isxvd;
            this._idObj = other._idObj;
        }

        public FieldInfo(RecordInputStream in) {
            this._isxvi = in.readShort();
            this._isxvd = in.readShort();
            this._idObj = in.readShort();
        }

        private void serialize(LittleEndianOutput out) {
            out.writeShort(this._isxvi);
            out.writeShort(this._isxvd);
            out.writeShort(this._idObj);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("isxvi", () -> this._isxvi, "isxvd", () -> this._isxvd, "idObj", () -> this._idObj);
        }
    }
}

