/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class MergeCellsRecord
extends StandardRecord {
    public static final short sid = 229;
    private final CellRangeAddress[] _regions;
    private final int _startIndex;
    private final int _numberOfRegions;

    public MergeCellsRecord(MergeCellsRecord other) {
        super(other);
        this._regions = other._regions == null ? null : (CellRangeAddress[])Stream.of(other._regions).map(CellRangeAddress::copy).toArray(CellRangeAddress[]::new);
        this._startIndex = other._startIndex;
        this._numberOfRegions = other._numberOfRegions;
    }

    public MergeCellsRecord(CellRangeAddress[] regions, int startIndex, int numberOfRegions) {
        this._regions = regions;
        this._startIndex = startIndex;
        this._numberOfRegions = numberOfRegions;
    }

    public MergeCellsRecord(RecordInputStream in) {
        int nRegions = in.readUShort();
        CellRangeAddress[] cras = new CellRangeAddress[nRegions];
        for (int i = 0; i < nRegions; ++i) {
            cras[i] = new CellRangeAddress(in);
        }
        this._numberOfRegions = nRegions;
        this._startIndex = 0;
        this._regions = cras;
    }

    public short getNumAreas() {
        return (short)this._numberOfRegions;
    }

    public CellRangeAddress getAreaAt(int index) {
        return this._regions[this._startIndex + index];
    }

    @Override
    protected int getDataSize() {
        return CellRangeAddressList.getEncodedSize(this._numberOfRegions);
    }

    @Override
    public short getSid() {
        return 229;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._numberOfRegions);
        for (int i = 0; i < this._numberOfRegions; ++i) {
            this._regions[this._startIndex + i].serialize(out);
        }
    }

    @Override
    public MergeCellsRecord copy() {
        return new MergeCellsRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.MERGE_CELLS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("numRegions", this::getNumAreas, "regions", () -> Arrays.copyOfRange(this._regions, this._startIndex, this._startIndex + this._numberOfRegions));
    }
}

