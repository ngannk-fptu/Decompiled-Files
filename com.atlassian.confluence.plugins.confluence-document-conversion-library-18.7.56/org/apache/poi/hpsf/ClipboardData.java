/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.LittleEndianByteArrayInputStream;

@Internal
public class ClipboardData {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    private static int MAX_RECORD_LENGTH = 100000000;
    private static final Logger LOG = LogManager.getLogger(ClipboardData.class);
    private int _format;
    private byte[] _value;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public void read(LittleEndianByteArrayInputStream lei) {
        int offset = lei.getReadIndex();
        long size = lei.readInt();
        if (size < 4L) {
            LOG.atWarn().log("ClipboardData at offset {} size less than 4 bytes (doesn't even have format field!). Setting to format == 0 and hope for the best", (Object)Unbox.box(offset));
            this._format = 0;
            this._value = new byte[0];
            return;
        }
        this._format = lei.readInt();
        this._value = IOUtils.safelyAllocate(size - 4L, MAX_RECORD_LENGTH);
        lei.readFully(this._value);
    }

    public byte[] getValue() {
        return this._value;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[8 + this._value.length];
        LittleEndian.putInt(result, 0, 4 + this._value.length);
        LittleEndian.putInt(result, 4, this._format);
        System.arraycopy(this._value, 0, result, 8, this._value.length);
        return result;
    }

    public void setValue(byte[] value) {
        this._value = (byte[])value.clone();
    }
}

