/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.modzdetector;

import com.atlassian.modzdetector.HashAlgorithm;
import com.atlassian.modzdetector.IOUtils;
import com.atlassian.modzdetector.NullOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

public class Adler32HashAlgorithm
implements HashAlgorithm {
    public String getHash(InputStream stream) {
        try {
            Adler32 adler = new Adler32();
            CheckedInputStream cis = new CheckedInputStream(stream, adler);
            IOUtils.copy(cis, new NullOutputStream());
            return Long.toHexString(adler.getValue());
        }
        catch (IOException e) {
            return null;
        }
    }

    public String getHash(byte[] bytes) {
        Adler32 adler = new Adler32();
        adler.update(bytes);
        return Long.toHexString(adler.getValue());
    }

    public String toString() {
        return "ADLER32 HEX";
    }
}

