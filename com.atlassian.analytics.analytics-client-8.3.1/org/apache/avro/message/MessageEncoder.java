/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.message;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface MessageEncoder<D> {
    public ByteBuffer encode(D var1) throws IOException;

    public void encode(D var1, OutputStream var2) throws IOException;
}

