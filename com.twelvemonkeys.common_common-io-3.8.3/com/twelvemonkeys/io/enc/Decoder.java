/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface Decoder {
    public int decode(InputStream var1, ByteBuffer var2) throws IOException;
}

