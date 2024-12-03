/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

public interface AsyncContextCallback {
    public void fireOnComplete();

    public boolean isAvailable();

    public void incrementInProgressAsyncCount();

    public void decrementInProgressAsyncCount();
}

