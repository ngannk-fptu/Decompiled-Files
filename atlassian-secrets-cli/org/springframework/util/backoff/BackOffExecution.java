/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util.backoff;

@FunctionalInterface
public interface BackOffExecution {
    public static final long STOP = -1L;

    public long nextBackOff();
}

