/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.jvnet.fastinfoset.EncodingAlgorithmException;

public interface EncodingAlgorithm {
    public Object decodeFromBytes(byte[] var1, int var2, int var3) throws EncodingAlgorithmException;

    public Object decodeFromInputStream(InputStream var1) throws EncodingAlgorithmException, IOException;

    public void encodeToOutputStream(Object var1, OutputStream var2) throws EncodingAlgorithmException, IOException;

    public Object convertFromCharacters(char[] var1, int var2, int var3) throws EncodingAlgorithmException;

    public void convertToCharacters(Object var1, StringBuffer var2) throws EncodingAlgorithmException;
}

