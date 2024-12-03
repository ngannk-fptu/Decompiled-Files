/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

public interface Lifecycle {
    public void start();

    public void stop();

    public boolean isRunning();
}

