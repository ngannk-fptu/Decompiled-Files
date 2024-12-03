/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.SSTDeserializer;
import org.apache.poi.hssf.record.SSTSerializer;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.cont.ContinuableRecord;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IntMapper;

public final class SSTRecord
extends ContinuableRecord {
    public static final short sid = 252;
    private static final UnicodeString EMPTY_STRING = new UnicodeString("");
    private int field_1_num_strings;
    private int field_2_num_unique_strings;
    private final IntMapper<UnicodeString> field_3_strings;
    private final SSTDeserializer deserializer;
    private int[] bucketAbsoluteOffsets;
    private int[] bucketRelativeOffsets;

    public SSTRecord() {
        this.field_1_num_strings = 0;
        this.field_2_num_unique_strings = 0;
        this.field_3_strings = new IntMapper();
        this.deserializer = new SSTDeserializer(this.field_3_strings);
    }

    public SSTRecord(SSTRecord other) {
        super(other);
        this.field_1_num_strings = other.field_1_num_strings;
        this.field_2_num_unique_strings = other.field_2_num_unique_strings;
        this.field_3_strings = other.field_3_strings.copy();
        this.deserializer = new SSTDeserializer(this.field_3_strings);
        this.bucketAbsoluteOffsets = other.bucketAbsoluteOffsets == null ? null : (int[])other.bucketAbsoluteOffsets.clone();
        this.bucketRelativeOffsets = other.bucketRelativeOffsets == null ? null : (int[])other.bucketRelativeOffsets.clone();
    }

    public int addString(UnicodeString string) {
        int rval;
        ++this.field_1_num_strings;
        UnicodeString ucs = string == null ? EMPTY_STRING : string;
        int index = this.field_3_strings.getIndex(ucs);
        if (index != -1) {
            rval = index;
        } else {
            rval = this.field_3_strings.size();
            ++this.field_2_num_unique_strings;
            SSTDeserializer.addToStringTable(this.field_3_strings, ucs);
        }
        return rval;
    }

    public int getNumStrings() {
        return this.field_1_num_strings;
    }

    public int getNumUniqueStrings() {
        return this.field_2_num_unique_strings;
    }

    public UnicodeString getString(int id) {
        return this.field_3_strings.get(id);
    }

    @Override
    public short getSid() {
        return 252;
    }

    public SSTRecord(RecordInputStream in) {
        this.field_1_num_strings = in.readInt();
        this.field_2_num_unique_strings = in.readInt();
        this.field_3_strings = new IntMapper();
        this.deserializer = new SSTDeserializer(this.field_3_strings);
        if (this.field_1_num_strings == 0) {
            this.field_2_num_unique_strings = 0;
            return;
        }
        this.deserializer.manufactureStrings(this.field_2_num_unique_strings, in);
    }

    Iterator<UnicodeString> getStrings() {
        return this.field_3_strings.iterator();
    }

    int countStrings() {
        return this.field_3_strings.size();
    }

    @Override
    protected void serialize(ContinuableRecordOutput out) {
        SSTSerializer serializer = new SSTSerializer(this.field_3_strings, this.getNumStrings(), this.getNumUniqueStrings());
        serializer.serialize(out);
        this.bucketAbsoluteOffsets = serializer.getBucketAbsoluteOffsets();
        this.bucketRelativeOffsets = serializer.getBucketRelativeOffsets();
    }

    public ExtSSTRecord createExtSSTRecord(int sstOffset) {
        if (this.bucketAbsoluteOffsets == null || this.bucketRelativeOffsets == null) {
            throw new IllegalStateException("SST record has not yet been serialized.");
        }
        ExtSSTRecord extSST = new ExtSSTRecord();
        extSST.setNumStringsPerBucket((short)8);
        int[] absoluteOffsets = (int[])this.bucketAbsoluteOffsets.clone();
        int[] relativeOffsets = (int[])this.bucketRelativeOffsets.clone();
        int i = 0;
        while (i < absoluteOffsets.length) {
            int n = i++;
            absoluteOffsets[n] = absoluteOffsets[n] + sstOffset;
        }
        extSST.setBucketOffsets(absoluteOffsets, relativeOffsets);
        return extSST;
    }

    public int calcExtSSTRecordSize() {
        return ExtSSTRecord.getRecordSizeForStrings(this.field_3_strings.size());
    }

    @Override
    public SSTRecord copy() {
        return new SSTRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SST;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("numStrings", this::getNumStrings, "numUniqueStrings", this::getNumUniqueStrings, "strings", this.field_3_strings::getElements, "bucketAbsoluteOffsets", () -> this.bucketAbsoluteOffsets, "bucketRelativeOffsets", () -> this.bucketRelativeOffsets);
    }
}

