/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.cont.ContinuableRecord;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ExtSSTRecord
extends ContinuableRecord {
    public static final short sid = 255;
    public static final int DEFAULT_BUCKET_SIZE = 8;
    public static final int MAX_BUCKETS = 128;
    private short _stringsPerBucket;
    private InfoSubRecord[] _sstInfos;

    public ExtSSTRecord() {
        this._stringsPerBucket = (short)8;
        this._sstInfos = new InfoSubRecord[0];
    }

    public ExtSSTRecord(ExtSSTRecord other) {
        this._stringsPerBucket = other._stringsPerBucket;
        this._sstInfos = other._sstInfos == null ? null : (InfoSubRecord[])Stream.of(other._sstInfos).map(InfoSubRecord::new).toArray(InfoSubRecord[]::new);
    }

    public ExtSSTRecord(RecordInputStream in) {
        this._stringsPerBucket = in.readShort();
        int nInfos = in.remaining() / 8;
        ArrayList<InfoSubRecord> lst = new ArrayList<InfoSubRecord>(nInfos);
        while (in.available() > 0) {
            InfoSubRecord info = new InfoSubRecord(in);
            lst.add(info);
            if (in.available() != 0 || !in.hasNextRecord() || in.getNextSid() != 60) continue;
            in.nextRecord();
        }
        this._sstInfos = lst.toArray(new InfoSubRecord[0]);
    }

    public void setNumStringsPerBucket(short numStrings) {
        this._stringsPerBucket = numStrings;
    }

    @Override
    public void serialize(ContinuableRecordOutput out) {
        out.writeShort(this._stringsPerBucket);
        for (InfoSubRecord sstInfo : this._sstInfos) {
            sstInfo.serialize(out);
        }
    }

    int getDataSize() {
        return 2 + 8 * this._sstInfos.length;
    }

    InfoSubRecord[] getInfoSubRecords() {
        return this._sstInfos;
    }

    public static int getNumberOfInfoRecsForStrings(int numStrings) {
        int infoRecs = numStrings / 8;
        if (numStrings % 8 != 0) {
            ++infoRecs;
        }
        if (infoRecs > 128) {
            infoRecs = 128;
        }
        return infoRecs;
    }

    public static int getRecordSizeForStrings(int numStrings) {
        return 6 + ExtSSTRecord.getNumberOfInfoRecsForStrings(numStrings) * 8;
    }

    @Override
    public short getSid() {
        return 255;
    }

    public void setBucketOffsets(int[] bucketAbsoluteOffsets, int[] bucketRelativeOffsets) {
        this._sstInfos = new InfoSubRecord[bucketAbsoluteOffsets.length];
        for (int i = 0; i < bucketAbsoluteOffsets.length; ++i) {
            this._sstInfos[i] = new InfoSubRecord(bucketAbsoluteOffsets[i], bucketRelativeOffsets[i]);
        }
    }

    @Override
    public ExtSSTRecord copy() {
        return new ExtSSTRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.EXT_SST;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("dataSize", this::getDataSize, "infoSubRecords", this::getInfoSubRecords);
    }

    public static final class InfoSubRecord
    implements GenericRecord {
        public static final int ENCODED_SIZE = 8;
        private int field_1_stream_pos;
        private int field_2_bucket_sst_offset;
        private short field_3_zero;

        public InfoSubRecord(int streamPos, int bucketSstOffset) {
            this.field_1_stream_pos = streamPos;
            this.field_2_bucket_sst_offset = bucketSstOffset;
        }

        public InfoSubRecord(InfoSubRecord other) {
            this.field_1_stream_pos = other.field_1_stream_pos;
            this.field_2_bucket_sst_offset = other.field_2_bucket_sst_offset;
            this.field_3_zero = other.field_3_zero;
        }

        public InfoSubRecord(RecordInputStream in) {
            this.field_1_stream_pos = in.readInt();
            this.field_2_bucket_sst_offset = in.readShort();
            this.field_3_zero = in.readShort();
        }

        public int getStreamPos() {
            return this.field_1_stream_pos;
        }

        public int getBucketSSTOffset() {
            return this.field_2_bucket_sst_offset;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeInt(this.field_1_stream_pos);
            out.writeShort(this.field_2_bucket_sst_offset);
            out.writeShort(this.field_3_zero);
        }

        @Override
        public Map<String, Supplier<?>> getGenericProperties() {
            return GenericRecordUtil.getGenericProperties("streamPos", this::getStreamPos, "bucketSSTOffset", this::getBucketSSTOffset);
        }
    }
}

