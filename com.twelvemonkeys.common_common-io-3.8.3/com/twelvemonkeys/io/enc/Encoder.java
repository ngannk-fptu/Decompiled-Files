/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.enc;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface Encoder {
    public void encode(OutputStream var1, ByteBuffer var2) throws IOException;
}

