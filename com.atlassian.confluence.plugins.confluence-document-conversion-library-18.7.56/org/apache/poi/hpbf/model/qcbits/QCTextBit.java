/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpbf.model.qcbits;

import org.apache.poi.hpbf.model.qcbits.QCBit;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.StringUtil;

public final class QCTextBit
extends QCBit {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public QCTextBit(String thingType, String bitType, byte[] data) {
        super(thingType, bitType, data);
    }

    public String getText() {
        return StringUtil.getFromUnicodeLE(this.getData());
    }

    public void setText(String text) {
        byte[] data = IOUtils.safelyAllocate((long)text.length() * 2L, MAX_RECORD_LENGTH);
        StringUtil.putUnicodeLE(text, data, 0);
        this.setData(data);
    }
}

