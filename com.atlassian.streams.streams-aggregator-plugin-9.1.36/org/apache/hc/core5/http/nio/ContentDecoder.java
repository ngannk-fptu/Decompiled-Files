/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.hc.core5.http.Header;

public interface ContentDecoder {
    public int read(ByteBuffer var1) throws IOException;

    public boolean isCompleted();

    public List<? extends Header> getTrailers();
}

