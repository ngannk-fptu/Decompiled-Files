/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.model;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hpbf.model.HPBFPart;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.IOUtils;

public abstract class EscherPart
extends HPBFPart {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    private EscherRecord[] records;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public EscherPart(DirectoryNode baseDir, String[] parts) throws IOException {
        super(baseDir, parts);
        EscherRecord er;
        DefaultEscherRecordFactory erf = new DefaultEscherRecordFactory();
        ArrayList<EscherRecord> ec = new ArrayList<EscherRecord>();
        byte[] data = this.getData();
        for (int left = data.length; left > 0; left -= er.getRecordSize()) {
            er = erf.createRecord(data, 0);
            er.fillFields(data, 0, erf);
            ec.add(er);
        }
        this.records = ec.toArray(new EscherRecord[0]);
    }

    public EscherRecord[] getEscherRecords() {
        return this.records;
    }

    @Override
    protected void generateData() {
        int size = 0;
        for (EscherRecord escherRecord : this.records) {
            size += escherRecord.getRecordSize();
        }
        byte[] data = IOUtils.safelyAllocate(size, MAX_RECORD_LENGTH);
        size = 0;
        for (EscherRecord record : this.records) {
            int thisSize = record.serialize(size, data);
            size += thisSize;
        }
        this.setData(data);
    }
}

