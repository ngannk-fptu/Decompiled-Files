/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface OutputBuffer {
    public int doWrite(ByteBuffer var1) throws IOException;

    public long getBytesWritten();
}

