/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.config;

import com.atlassian.seraph.config.LoginUrlStrategy;
import com.atlassian.seraph.config.SecurityConfig;
import java.util.Map;

public class DefaultLoginUrlStrategy
implements LoginUrlStrategy {
    @Override
    public void init(Map<String, String> params, SecurityConfig config) {
    }

    @Override
    public String getLoginURL(SecurityConfig config, String configuredLoginUrl) {
        return configuredLoginUrl;
    }

    @Override
    public String getLogoutURL(SecurityConfig config, String configuredLogoutUrl) {
        return configuredLogoutUrl;
    }

    @Override
    public String getLinkLoginURL(SecurityConfig config, String configuredLinkLoginUrl) {
        return configuredLinkLoginUrl;
    }
}

