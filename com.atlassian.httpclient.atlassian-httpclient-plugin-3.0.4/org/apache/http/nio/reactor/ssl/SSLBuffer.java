/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.reactor.ssl;

import java.nio.ByteBuffer;

public interface SSLBuffer {
    public ByteBuffer acquire();

    public void release();

    public boolean isAcquired();

    public boolean hasData();
}

