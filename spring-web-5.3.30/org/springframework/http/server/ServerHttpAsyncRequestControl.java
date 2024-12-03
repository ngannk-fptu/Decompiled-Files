/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server;

public interface ServerHttpAsyncRequestControl {
    public void start();

    public void start(long var1);

    public boolean isStarted();

    public void complete();

    public boolean isCompleted();
}

