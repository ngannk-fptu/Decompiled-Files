/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.reactor.ssl;

import org.apache.http.nio.reactor.ssl.SSLBuffer;

public interface SSLBufferManagementStrategy {
    public SSLBuffer constructBuffer(int var1);
}

