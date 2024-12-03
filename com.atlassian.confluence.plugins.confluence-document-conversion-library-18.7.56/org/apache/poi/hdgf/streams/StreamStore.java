/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.streams;

import org.apache.poi.util.IOUtils;

public class StreamStore {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 10000000;
    private static int MAX_RECORD_LENGTH = 10000000;
    private byte[] contents;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected StreamStore(byte[] data, int offset, int length) {
        this.contents = IOUtils.safelyClone(data, offset, length, MAX_RECORD_LENGTH);
    }

    protected void prependContentsWith(byte[] b) {
        byte[] newContents = IOUtils.safelyAllocate((long)this.contents.length + (long)b.length, MAX_RECORD_LENGTH);
        System.arraycopy(b, 0, newContents, 0, b.length);
        System.arraycopy(this.contents, 0, newContents, b.length, this.contents.length);
        this.contents = newContents;
    }

    protected void copyBlockHeaderToContents() {
    }

    protected byte[] getContents() {
        return this.contents;
    }

    public byte[] _getContents() {
        return this.contents;
    }
}

