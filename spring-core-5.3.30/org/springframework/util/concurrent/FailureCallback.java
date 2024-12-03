/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util.concurrent;

@FunctionalInterface
public interface FailureCallback {
    public void onFailure(Throwable var1);
}

