/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

public interface FailureCache {
    public int getErrorCount(String var1);

    public void resetErrorCount(String var1);

    public void increaseErrorCount(String var1);
}

