/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.core.net.SystemPropertiesConnectionConfig
 */
package com.atlassian.confluence.api.impl.sal.util;

import com.atlassian.confluence.util.http.ConfluenceHttpParameters;
import com.atlassian.sal.core.net.SystemPropertiesConnectionConfig;

public class HttpTimeoutParameters {
    public static int getSocketTimeoutFrom(SystemPropertiesConnectionConfig systemPropConfigs, ConfluenceHttpParameters confluenceHttpParameters) {
        if (System.getProperty("http.socketTimeout") != null) {
            return systemPropConfigs.getSocketTimeout();
        }
        return confluenceHttpParameters.getSocketTimeout();
    }

    public static int getConnectionTimeoutFrom(SystemPropertiesConnectionConfig systemPropConfigs, ConfluenceHttpParameters confluenceHttpParameters) {
        if (System.getProperty("http.connectionTimeout") != null) {
            return systemPropConfigs.getConnectionTimeout();
        }
        return confluenceHttpParameters.getConnectionTimeout();
    }
}

