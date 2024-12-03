/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherDggRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hslf.record.HSLFEscherRecordFactory;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class PPDrawingGroup
extends RecordAtom {
    private static final int MAX_RECORD_LENGTH = 0xA00000;
    private final byte[] _header;
    private final EscherContainerRecord dggContainer;
    private EscherDggRecord dgg;

    PPDrawingGroup(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        byte[] contents = IOUtils.safelyClone(source, start, len, 0xA00000);
        HSLFEscherRecordFactory erf = new HSLFEscherRecordFactory();
        EscherRecord child = erf.createRecord(contents, 0);
        child.fillFields(contents, 0, erf);
        this.dggContainer = (EscherContainerRecord)child.getChild(0);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.PPDrawingGroup.typeID;
    }

    @Override
    public Record[] getChildRecords() {
        return null;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        byte[] bstorehead = new byte[8];
        byte[] recordBytes = new byte[44];
        try (UnsynchronizedByteArrayOutputStream bout = new UnsynchronizedByteArrayOutputStream();
             UnsynchronizedByteArrayOutputStream recordBuf = new UnsynchronizedByteArrayOutputStream();){
            for (EscherRecord r : this.dggContainer) {
                if (r.getRecordId() == EscherContainerRecord.BSTORE_CONTAINER) {
                    EscherContainerRecord bstore = (EscherContainerRecord)r;
                    recordBuf.reset();
                    for (EscherRecord br : bstore) {
                        br.serialize(0, recordBytes);
                        recordBuf.write(recordBytes);
                    }
                    LittleEndian.putShort(bstorehead, 0, bstore.getOptions());
                    LittleEndian.putShort(bstorehead, 2, bstore.getRecordId());
                    LittleEndian.putInt(bstorehead, 4, recordBuf.size());
                    bout.write(bstorehead);
                    recordBuf.writeTo((OutputStream)bout);
                    continue;
                }
                bout.write(r.serialize());
            }
            int size = bout.size();
            LittleEndian.putInt(this._header, 4, size + 8);
            out.write(this._header);
            byte[] dgghead = new byte[8];
            LittleEndian.putShort(dgghead, 0, this.dggContainer.getOptions());
            LittleEndian.putShort(dgghead, 2, this.dggContainer.getRecordId());
            LittleEndian.putInt(dgghead, 4, size);
            out.write(dgghead);
            bout.writeTo(out);
        }
    }

    public EscherContainerRecord getDggContainer() {
        return this.dggContainer;
    }

    public EscherDggRecord getEscherDggRecord() {
        if (this.dgg == null) {
            for (EscherRecord r : this.dggContainer) {
                if (!(r instanceof EscherDggRecord)) continue;
                this.dgg = (EscherDggRecord)r;
                break;
            }
        }
        return this.dgg;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("dggContainer", this::getDggContainer);
    }
}

