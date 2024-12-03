/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.util.Map;

public interface FileOptionsProvider {
    public static final String CURRENT_USER = "currentUser";
    public static final String VERSIONING = "versioning";
    public static final String PROXY_HOST = "proxyHost";
    public static final String PROXY_PORT = "proxyPort";
    public static final String MAX_HOST_CONNECTIONS = "maxHostConnections";
    public static final String MAX_TOTAL_CONNECTIONS = "maxTotalConnections";

    public Map<String, Object> getOptions();
}

