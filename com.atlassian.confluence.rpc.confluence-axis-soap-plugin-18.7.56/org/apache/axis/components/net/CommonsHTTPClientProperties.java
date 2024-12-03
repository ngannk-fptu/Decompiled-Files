/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.net;

public interface CommonsHTTPClientProperties {
    public int getMaximumTotalConnections();

    public int getMaximumConnectionsPerHost();

    public int getConnectionPoolTimeout();

    public int getDefaultConnectionTimeout();

    public int getDefaultSoTimeout();
}

