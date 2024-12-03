/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.core.net;

public interface ConnectionConfig {
    public int getSocketTimeout();

    public int getConnectionTimeout();

    public int getMaxRedirects();
}

