/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ContentDecoder {
    public int read(ByteBuffer var1) throws IOException;

    public boolean isCompleted();
}

