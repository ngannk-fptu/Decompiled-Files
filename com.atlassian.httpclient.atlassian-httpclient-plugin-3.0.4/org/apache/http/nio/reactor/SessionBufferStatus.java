/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.reactor;

public interface SessionBufferStatus {
    public boolean hasBufferedInput();

    public boolean hasBufferedOutput();
}

