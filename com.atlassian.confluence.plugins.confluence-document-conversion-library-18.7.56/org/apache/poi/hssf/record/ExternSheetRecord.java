/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public class ExternSheetRecord
extends StandardRecord {
    public static final short sid = 23;
    private final List<RefSubRecord> _list = new ArrayList<RefSubRecord>();

    public ExternSheetRecord() {
    }

    public ExternSheetRecord(ExternSheetRecord other) {
        other._list.stream().map(RefSubRecord::new).forEach(this._list::add);
    }

    public ExternSheetRecord(RecordInputStream in) {
        int nItems = in.readShort();
        for (int i = 0; i < nItems; ++i) {
            RefSubRecord rec = new RefSubRecord(in);
            this._list.add(rec);
        }
    }

    public int getNumOfRefs() {
        return this._list.size();
    }

    public void addREFRecord(RefSubRecord rec) {
        this._list.add(rec);
    }

    public int getNumOfREFRecords() {
        return this._list.size();
    }

    @Override
    protected int getDataSize() {
        return 2 + this._list.size() * 6;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        int nItems = this._list.size();
        out.writeShort(nItems);
        for (int i = 0; i < nItems; ++i) {
            this.getRef(i).serialize(out);
        }
    }

    private RefSubRecord getRef(int i) {
        return this._list.get(i);
    }

    public void removeSheet(int sheetIdx) {
        int nItems = this._list.size();
        for (int i = 0; i < nItems; ++i) {
            RefSubRecord refSubRecord = this._list.get(i);
            if (refSubRecord.getFirstSheetIndex() == sheetIdx && refSubRecord.getLastSheetIndex() == sheetIdx) {
                this._list.set(i, new RefSubRecord(refSubRecord.getExtBookIndex(), -1, -1));
                continue;
            }
            if (refSubRecord.getFirstSheetIndex() <= sheetIdx || refSubRecord.getLastSheetIndex() <= sheetIdx) continue;
            this._list.set(i, new RefSubRecord(refSubRecord.getExtBookIndex(), refSubRecord.getFirstSheetIndex() - 1, refSubRecord.getLastSheetIndex() - 1));
        }
    }

    @Override
    public short getSid() {
        return 23;
    }

    public int getExtbookIndexFromRefIndex(int refIndex) {
        RefSubRecord refRec = this.getRef(refIndex);
        return refRec.getExtBookIndex();
    }

    public int findRefIndexFromExtBookIndex(int extBookIndex) {
        int nItems = this._list.size();
        for (int i = 0; i < nItems; ++i) {
            if (this.getRef(i).getExtBookIndex() != extBookIndex) continue;
            return i;
        }
        return -1;
    }

    public int getFirstSheetIndexFromRefIndex(int extRefIndex) {
        return this.getRef(extRefIndex).getFirstSheetIndex();
    }

    public int getLastSheetIndexFromRefIndex(int extRefIndex) {
        return this.getRef(extRefIndex).getLastSheetIndex();
    }

    public int addRef(int extBookIndex, int firstSheetIndex, int lastSheetIndex) {
        this._list.add(new RefSubRecord(extBookIndex, firstSheetIndex, lastSheetIndex));
        return this._list.size() - 1;
    }

    public int getRefIxForSheet(int externalBookIndex, int firstSheetIndex, int lastSheetIndex) {
        int nItems = this._list.size();
        for (int i = 0; i < nItems; ++i) {
            RefSubRecord ref = this.getRef(i);
            if (ref.getExtBookIndex() != externalBookIndex || ref.getFirstSheetIndex() != firstSheetIndex || ref.getLastSheetIndex() != lastSheetIndex) continue;
            return i;
        }
        return -1;
    }

    public static ExternSheetRecord combine(ExternSheetRecord[] esrs) {
        ExternSheetRecord result = new ExternSheetRecord();
        for (ExternSheetRecord esr : esrs) {
            int nRefs = esr.getNumOfREFRecords();
            for (int j = 0; j < nRefs; ++j) {
                result.addREFRecord(esr.getRef(j));
            }
        }
        return result;
    }

    @Override
    public ExternSheetRecord copy() {
        return new ExternSheetRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.EXTERN_SHEET;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("refrec", () -> this._list);
    }

    private static final class RefSubRecord
    implements GenericRecord {
        public static final int ENCODED_SIZE = 6;
        private final int _extBookIndex;
        private int _firstSheetIndex;
        private int _lastSheetIndex;

        public RefSubRecord(int extBookIndex, int firstSheetIndex, int lastSheetIndex) {
            this._extBookIndex = extBookIndex;
            this._firstSheetIndex = firstSheetIndex;
            this._lastSheetIndex = lastSheetIndex;
        }

        public RefSubRecord(RefSubRecord other) {
            this._extBookIndex = other._extBookIndex;
            this._firstSheetIndex = other._firstSheetIndex;
            this._lastSheetIndex = other._lastSheetIndex;
        }

        public RefSubRecord(RecordInputStream in) {
            this(in.readShort(), in.readShort(), in.readShort());
        }

        public int getExtBookIndex() {
            return this._extBookIndex;
        }

        public int getFirstSheetIndex() {
            return this._firstSheetIndex;
        }

        public int getLastSheetIndex() {
            return this._lastSheetIndex;
        }

        public String toString() {
            return GenericRecordJsonWriter.marshal(this);
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(this._extBookIndex);
            out.writeShort(this._firstSheetIndex);
            out.writeShort(this._lastSheetIndex);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("extBookIndex", this::getExtBookIndex, "firstSheetIndex", this::getFirstSheetIndex, "lastSheetIndex", this::getLastSheetIndex);
        }
    }
}

