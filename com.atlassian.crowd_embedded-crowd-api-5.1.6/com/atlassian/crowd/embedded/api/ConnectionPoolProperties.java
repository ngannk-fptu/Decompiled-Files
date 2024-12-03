/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

import java.util.Map;

public interface ConnectionPoolProperties {
    public String getInitialSize();

    public String getMaximumSize();

    public String getPreferredSize();

    public String getTimeoutInSec();

    public String getSupportedAuthentication();

    public String getSupportedProtocol();

    public Map<String, String> toPropertiesMap();
}

