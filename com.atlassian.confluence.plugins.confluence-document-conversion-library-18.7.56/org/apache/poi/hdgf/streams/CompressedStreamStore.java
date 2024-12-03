/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.input.UnsynchronizedByteArrayInputStream
 */
package org.apache.poi.hdgf.streams;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.input.UnsynchronizedByteArrayInputStream;
import org.apache.poi.hdgf.HDGFLZW;
import org.apache.poi.hdgf.streams.StreamStore;
import org.apache.poi.util.IOUtils;

public final class CompressedStreamStore
extends StreamStore {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 64000000;
    private static int MAX_RECORD_LENGTH = 64000000;
    private byte[] compressedContents;
    private final byte[] blockHeader;
    private boolean blockHeaderInContents;

    byte[] _getCompressedContents() {
        return this.compressedContents;
    }

    byte[] _getBlockHeader() {
        return this.blockHeader;
    }

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    CompressedStreamStore(byte[] data, int offset, int length) throws IOException {
        this(CompressedStreamStore.decompress(data, offset, length));
        this.compressedContents = IOUtils.safelyClone(data, offset, length, MAX_RECORD_LENGTH);
    }

    private CompressedStreamStore(byte[][] decompressedData) {
        super(decompressedData[1], 0, decompressedData[1].length);
        this.blockHeader = decompressedData[0];
    }

    @Override
    protected void copyBlockHeaderToContents() {
        if (this.blockHeaderInContents) {
            return;
        }
        this.prependContentsWith(this.blockHeader);
        this.blockHeaderInContents = true;
    }

    public static byte[][] decompress(byte[] data, int offset, int length) throws IOException {
        try (UnsynchronizedByteArrayInputStream bais = new UnsynchronizedByteArrayInputStream(data, offset, length);){
            HDGFLZW lzw = new HDGFLZW();
            byte[] decompressed = lzw.decompress((InputStream)bais);
            if (decompressed.length < 4) {
                throw new IllegalArgumentException("Could not read enough data to decompress: " + decompressed.length);
            }
            byte[][] ret = new byte[][]{new byte[4], new byte[decompressed.length - 4]};
            System.arraycopy(decompressed, 0, ret[0], 0, 4);
            System.arraycopy(decompressed, 4, ret[1], 0, ret[1].length);
            byte[][] byArrayArray = ret;
            return byArrayArray;
        }
    }
}

