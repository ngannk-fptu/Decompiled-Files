/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

public interface Encoder {
    public int getEncodedLength(int var1);

    public int getMaxDecodedLength(int var1);

    public int encode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException;

    public int decode(byte[] var1, int var2, int var3, OutputStream var4) throws IOException;

    public int decode(String var1, OutputStream var2) throws IOException;
}

