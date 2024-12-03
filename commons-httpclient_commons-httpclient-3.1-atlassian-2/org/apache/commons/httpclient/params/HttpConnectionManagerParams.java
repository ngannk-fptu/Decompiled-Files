/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.params;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.params.HttpConnectionParams;

public class HttpConnectionManagerParams
extends HttpConnectionParams {
    public static final String MAX_HOST_CONNECTIONS = "http.connection-manager.max-per-host";
    public static final String MAX_TOTAL_CONNECTIONS = "http.connection-manager.max-total";

    public void setDefaultMaxConnectionsPerHost(int maxHostConnections) {
        this.setMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION, maxHostConnections);
    }

    public void setMaxConnectionsPerHost(HostConfiguration hostConfiguration, int maxHostConnections) {
        if (maxHostConnections <= 0) {
            throw new IllegalArgumentException("maxHostConnections must be greater than 0");
        }
        Map currentValues = (Map)this.getParameter(MAX_HOST_CONNECTIONS);
        HashMap<HostConfiguration, Integer> newValues = null;
        newValues = currentValues == null ? new HashMap<HostConfiguration, Integer>() : new HashMap(currentValues);
        newValues.put(hostConfiguration, new Integer(maxHostConnections));
        this.setParameter(MAX_HOST_CONNECTIONS, newValues);
    }

    public int getDefaultMaxConnectionsPerHost() {
        return this.getMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION);
    }

    public int getMaxConnectionsPerHost(HostConfiguration hostConfiguration) {
        Map m = (Map)this.getParameter(MAX_HOST_CONNECTIONS);
        if (m == null) {
            return 2;
        }
        Integer max = (Integer)m.get(hostConfiguration);
        if (max == null && hostConfiguration != HostConfiguration.ANY_HOST_CONFIGURATION) {
            return this.getMaxConnectionsPerHost(HostConfiguration.ANY_HOST_CONFIGURATION);
        }
        return max == null ? 2 : max;
    }

    public void setMaxTotalConnections(int maxTotalConnections) {
        this.setIntParameter(MAX_TOTAL_CONNECTIONS, maxTotalConnections);
    }

    public int getMaxTotalConnections() {
        return this.getIntParameter(MAX_TOTAL_CONNECTIONS, 20);
    }
}

