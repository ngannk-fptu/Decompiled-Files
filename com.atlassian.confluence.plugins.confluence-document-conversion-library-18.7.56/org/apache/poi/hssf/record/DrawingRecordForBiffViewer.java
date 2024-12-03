/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hssf.record;

import java.io.InputStream;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.hssf.record.AbstractEscherHolderRecord;
import org.apache.poi.hssf.record.DrawingRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;

public final class DrawingRecordForBiffViewer
extends AbstractEscherHolderRecord {
    public static final short sid = 236;

    public DrawingRecordForBiffViewer() {
    }

    public DrawingRecordForBiffViewer(DrawingRecordForBiffViewer other) {
        super(other);
    }

    public DrawingRecordForBiffViewer(RecordInputStream in) {
        super(in);
    }

    public DrawingRecordForBiffViewer(DrawingRecord r) {
        super(DrawingRecordForBiffViewer.convertToInputStream(r));
        this.decode();
    }

    private static RecordInputStream convertToInputStream(DrawingRecord r) {
        byte[] data = r.serialize();
        RecordInputStream rinp = new RecordInputStream((InputStream)new UnsynchronizedByteArrayInputStream(data));
        rinp.nextRecord();
        return rinp;
    }

    @Override
    protected String getRecordName() {
        return "MSODRAWING";
    }

    @Override
    public short getSid() {
        return 236;
    }

    @Override
    public DrawingRecordForBiffViewer copy() {
        return new DrawingRecordForBiffViewer(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DRAWING;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

