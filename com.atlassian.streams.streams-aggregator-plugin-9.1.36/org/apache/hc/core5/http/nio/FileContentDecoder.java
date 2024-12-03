/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import org.apache.hc.core5.http.nio.ContentDecoder;

public interface FileContentDecoder
extends ContentDecoder {
    public long transfer(FileChannel var1, long var2, long var4) throws IOException;
}

