/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.support.classic;

import java.io.IOException;

public interface ContentInputBuffer {
    public int length();

    public void reset();

    public int read(byte[] var1, int var2, int var3) throws IOException;

    public int read() throws IOException;
}

