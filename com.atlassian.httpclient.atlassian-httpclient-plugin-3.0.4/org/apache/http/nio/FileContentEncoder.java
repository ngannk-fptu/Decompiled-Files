/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio;

import java.io.IOException;
import java.nio.channels.FileChannel;
import org.apache.http.nio.ContentEncoder;

public interface FileContentEncoder
extends ContentEncoder {
    public long transfer(FileChannel var1, long var2, long var4) throws IOException;
}

