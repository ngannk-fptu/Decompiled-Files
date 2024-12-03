/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.HeaderFooterBase;
import org.apache.poi.hssf.record.RecordInputStream;

public final class HeaderRecord
extends HeaderFooterBase {
    public static final short sid = 20;

    public HeaderRecord(String text) {
        super(text);
    }

    public HeaderRecord(HeaderRecord other) {
        super(other);
    }

    public HeaderRecord(RecordInputStream in) {
        super(in);
    }

    @Override
    public short getSid() {
        return 20;
    }

    @Override
    public HeaderRecord copy() {
        return new HeaderRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.HEADER;
    }
}

