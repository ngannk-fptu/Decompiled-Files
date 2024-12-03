/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hdgf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hdgf.HDGFLZWCompressor;
import org.apache.poi.util.LZWDecompresser;

public class HDGFLZW
extends LZWDecompresser {
    public HDGFLZW() {
        super(false, 3, false);
    }

    public byte[] compress(InputStream src) throws IOException {
        UnsynchronizedByteArrayOutputStream res = new UnsynchronizedByteArrayOutputStream();
        this.compress(src, (OutputStream)res);
        return res.toByteArray();
    }

    @Override
    protected int adjustDictionaryOffset(int pntr) {
        pntr = pntr > 4078 ? (pntr -= 4078) : (pntr += 18);
        return pntr;
    }

    @Override
    protected int populateDictionary(byte[] dict) {
        return 0;
    }

    public void compress(InputStream src, OutputStream res) throws IOException {
        HDGFLZWCompressor c = new HDGFLZWCompressor(res);
        c.compress(src);
    }
}

