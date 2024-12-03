/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hpsf;

import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInput;

@Internal
public class Blob {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 10000000;
    private static int MAX_RECORD_LENGTH = 10000000;
    private byte[] _value;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    public void read(LittleEndianInput lei) {
        int size = lei.readInt();
        this._value = IOUtils.safelyAllocate(size, MAX_RECORD_LENGTH);
        if (size > 0) {
            lei.readFully(this._value);
        }
    }
}

