/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util.backoff;

import org.springframework.util.backoff.BackOffExecution;

@FunctionalInterface
public interface BackOff {
    public BackOffExecution start();
}

