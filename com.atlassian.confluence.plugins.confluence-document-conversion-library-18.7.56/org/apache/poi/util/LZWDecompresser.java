/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.util.IOUtils;

public abstract class LZWDecompresser {
    public static final int DICT_SIZE = 4096;
    public static final int DICT_MASK = 4095;
    private static final int DEFAULT_MAX_RECORD_LENGTH = 1000000;
    private static int MAX_RECORD_LENGTH = 1000000;
    private final boolean maskMeansCompressed;
    private final int codeLengthIncrease;
    private final boolean positionIsBigEndian;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected LZWDecompresser(boolean maskMeansCompressed, int codeLengthIncrease, boolean positionIsBigEndian) {
        this.maskMeansCompressed = maskMeansCompressed;
        this.codeLengthIncrease = codeLengthIncrease;
        this.positionIsBigEndian = positionIsBigEndian;
    }

    protected abstract int populateDictionary(byte[] var1);

    protected abstract int adjustDictionaryOffset(int var1);

    public byte[] decompress(InputStream src) throws IOException {
        UnsynchronizedByteArrayOutputStream res = new UnsynchronizedByteArrayOutputStream();
        this.decompress(src, (OutputStream)res);
        return res.toByteArray();
    }

    public void decompress(InputStream src, OutputStream res) throws IOException {
        int flag;
        byte[] buffer = new byte[4096];
        int pos = this.populateDictionary(buffer);
        byte[] dataB = IOUtils.safelyAllocate(16L + (long)this.codeLengthIncrease, MAX_RECORD_LENGTH);
        block0: while ((flag = src.read()) != -1) {
            for (int mask = 1; mask < 256; mask <<= 1) {
                boolean isMaskSet;
                boolean bl = isMaskSet = (flag & mask) > 0;
                if (isMaskSet ^ this.maskMeansCompressed) {
                    int dataI = src.read();
                    if (dataI == -1) continue;
                    buffer[pos++ & 0xFFF] = (byte)dataI;
                    res.write(dataI);
                    continue;
                }
                int dataIPt1 = src.read();
                int dataIPt2 = src.read();
                if (dataIPt1 == -1 || dataIPt2 == -1) continue block0;
                int len = (dataIPt2 & 0xF) + this.codeLengthIncrease;
                int pntr = this.positionIsBigEndian ? (dataIPt1 << 4) + (dataIPt2 >>> 4) : dataIPt1 + ((dataIPt2 & 0xF0) << 4);
                pntr = this.adjustDictionaryOffset(pntr);
                for (int i = 0; i < len; ++i) {
                    dataB[i] = buffer[pntr + i & 0xFFF];
                    buffer[pos + i & 0xFFF] = dataB[i];
                }
                res.write(dataB, 0, len);
                pos += len;
            }
        }
    }
}

