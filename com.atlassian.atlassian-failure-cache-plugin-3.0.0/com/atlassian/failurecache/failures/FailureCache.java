/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.failurecache.failures;

public interface FailureCache<K> {
    public boolean isFailing(K var1);

    public void registerSuccess(K var1);

    public void registerFailure(K var1);
}

