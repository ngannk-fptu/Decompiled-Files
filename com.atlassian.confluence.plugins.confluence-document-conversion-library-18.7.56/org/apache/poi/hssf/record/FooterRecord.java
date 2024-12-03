/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.HeaderFooterBase;
import org.apache.poi.hssf.record.RecordInputStream;

public final class FooterRecord
extends HeaderFooterBase {
    public static final short sid = 21;

    public FooterRecord(String text) {
        super(text);
    }

    public FooterRecord(FooterRecord other) {
        super(other);
    }

    public FooterRecord(RecordInputStream in) {
        super(in);
    }

    @Override
    public short getSid() {
        return 21;
    }

    @Override
    public FooterRecord copy() {
        return new FooterRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FOOTER;
    }
}

