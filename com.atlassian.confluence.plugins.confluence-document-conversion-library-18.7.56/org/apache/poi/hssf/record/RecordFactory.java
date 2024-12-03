/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.DBCellRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.MulRKRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactoryInputStream;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.RecordFormatException;

public final class RecordFactory {
    private static final int NUM_RECORDS = 512;
    private static final int DEFAULT_MAX_NUMBER_OF_RECORDS = 5000000;
    private static int MAX_NUMBER_OF_RECORDS = 5000000;

    public static void setMaxNumberOfRecords(int maxNumberOfRecords) {
        MAX_NUMBER_OF_RECORDS = maxNumberOfRecords;
    }

    public static int getMaxNumberOfRecords() {
        return MAX_NUMBER_OF_RECORDS;
    }

    private RecordFactory() {
    }

    public static Class<? extends Record> getRecordClass(int sid) {
        return HSSFRecordTypes.forSID((int)sid).clazz;
    }

    public static Record[] createRecord(RecordInputStream in) {
        Record record = RecordFactory.createSingleRecord(in);
        if (record instanceof DBCellRecord) {
            return new Record[]{null};
        }
        if (record instanceof RKRecord) {
            return new Record[]{RecordFactory.convertToNumberRecord((RKRecord)record)};
        }
        if (record instanceof MulRKRecord) {
            return RecordFactory.convertRKRecords((MulRKRecord)record);
        }
        return new Record[]{record};
    }

    public static Record createSingleRecord(RecordInputStream in) {
        HSSFRecordTypes rec = HSSFRecordTypes.forSID(in.getSid());
        if (!rec.isParseable()) {
            rec = HSSFRecordTypes.UNKNOWN;
        }
        return rec.recordConstructor.apply(in);
    }

    public static NumberRecord convertToNumberRecord(RKRecord rk) {
        NumberRecord num = new NumberRecord();
        num.setColumn(rk.getColumn());
        num.setRow(rk.getRow());
        num.setXFIndex(rk.getXFIndex());
        num.setValue(rk.getRKNumber());
        return num;
    }

    public static NumberRecord[] convertRKRecords(MulRKRecord mrk) {
        int numColumns = mrk.getNumColumns();
        if (numColumns < 0) {
            throw new RecordFormatException("Cannot create RKRecords with negative number of columns: " + numColumns);
        }
        NumberRecord[] mulRecs = new NumberRecord[numColumns];
        for (int k = 0; k < numColumns; ++k) {
            NumberRecord nr = new NumberRecord();
            nr.setColumn((short)(k + mrk.getFirstColumn()));
            nr.setRow(mrk.getRow());
            nr.setXFIndex(mrk.getXFAt(k));
            nr.setValue(mrk.getRKNumberAt(k));
            mulRecs[k] = nr;
        }
        return mulRecs;
    }

    public static BlankRecord[] convertBlankRecords(MulBlankRecord mbk) {
        BlankRecord[] mulRecs = new BlankRecord[mbk.getNumColumns()];
        for (int k = 0; k < mbk.getNumColumns(); ++k) {
            BlankRecord br = new BlankRecord();
            br.setColumn((short)(k + mbk.getFirstColumn()));
            br.setRow(mbk.getRow());
            br.setXFIndex(mbk.getXFAt(k));
            mulRecs[k] = br;
        }
        return mulRecs;
    }

    public static short[] getAllKnownRecordSIDs() {
        int[] intSid = Arrays.stream(HSSFRecordTypes.values()).mapToInt(HSSFRecordTypes::getSid).toArray();
        short[] shortSid = new short[intSid.length];
        for (int i = 0; i < intSid.length; ++i) {
            shortSid[i] = (short)intSid[i];
        }
        return shortSid;
    }

    public static List<Record> createRecords(InputStream in) throws RecordFormatException {
        Record record;
        ArrayList<Record> records = new ArrayList<Record>(512);
        RecordFactoryInputStream recStream = new RecordFactoryInputStream(in, true);
        while ((record = recStream.nextRecord()) != null) {
            records.add(record);
            IOUtils.safelyAllocateCheck(records.size(), MAX_NUMBER_OF_RECORDS);
        }
        return records;
    }
}

