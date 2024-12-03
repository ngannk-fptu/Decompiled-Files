/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.net;

import org.apache.axis.AxisProperties;
import org.apache.axis.components.net.CommonsHTTPClientProperties;

public class DefaultCommonsHTTPClientProperties
implements CommonsHTTPClientProperties {
    public static final String MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY = "axis.http.client.maximum.total.connections";
    public static final String MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY = "axis.http.client.maximum.connections.per.host";
    public static final String CONNECTION_POOL_TIMEOUT_KEY = "axis.http.client.connection.pool.timeout";
    public static final String CONNECTION_DEFAULT_CONNECTION_TIMEOUT_KEY = "axis.http.client.connection.default.connection.timeout";
    public static final String CONNECTION_DEFAULT_SO_TIMEOUT_KEY = "axis.http.client.connection.default.so.timeout";

    protected final int getIntegerProperty(String property, String dephault) {
        return Integer.parseInt(AxisProperties.getProperty(property, dephault));
    }

    public int getMaximumTotalConnections() {
        int i = this.getIntegerProperty(MAXIMUM_TOTAL_CONNECTIONS_PROPERTY_KEY, "20");
        if (i < 1) {
            throw new IllegalStateException("axis.http.client.maximum.total.connections must be > 1");
        }
        return i;
    }

    public int getMaximumConnectionsPerHost() {
        int i = this.getIntegerProperty(MAXIMUM_CONNECTIONS_PER_HOST_PROPERTY_KEY, "2");
        if (i < 1) {
            throw new IllegalStateException("axis.http.client.maximum.connections.per.host must be > 1");
        }
        return i;
    }

    public int getConnectionPoolTimeout() {
        int i = this.getIntegerProperty(CONNECTION_POOL_TIMEOUT_KEY, "0");
        if (i < 0) {
            throw new IllegalStateException("axis.http.client.connection.pool.timeout must be >= 0");
        }
        return i;
    }

    public int getDefaultConnectionTimeout() {
        int i = this.getIntegerProperty(CONNECTION_DEFAULT_CONNECTION_TIMEOUT_KEY, "0");
        if (i < 0) {
            throw new IllegalStateException("axis.http.client.connection.default.connection.timeout must be >= 0");
        }
        return i;
    }

    public int getDefaultSoTimeout() {
        int i = this.getIntegerProperty(CONNECTION_DEFAULT_SO_TIMEOUT_KEY, "0");
        if (i < 0) {
            throw new IllegalStateException("axis.http.client.connection.default.so.timeout must be >= 0");
        }
        return i;
    }
}

