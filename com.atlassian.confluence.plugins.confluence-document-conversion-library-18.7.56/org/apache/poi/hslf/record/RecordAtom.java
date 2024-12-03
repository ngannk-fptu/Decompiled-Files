/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import org.apache.poi.hslf.record.Record;

public abstract class RecordAtom
extends Record {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    @Override
    public boolean isAnAtom() {
        return true;
    }

    @Override
    public Record[] getChildRecords() {
        return null;
    }
}

