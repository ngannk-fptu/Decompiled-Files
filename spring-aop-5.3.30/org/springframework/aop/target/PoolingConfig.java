/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.target;

public interface PoolingConfig {
    public int getMaxSize();

    public int getActiveCount() throws UnsupportedOperationException;

    public int getIdleCount() throws UnsupportedOperationException;
}

