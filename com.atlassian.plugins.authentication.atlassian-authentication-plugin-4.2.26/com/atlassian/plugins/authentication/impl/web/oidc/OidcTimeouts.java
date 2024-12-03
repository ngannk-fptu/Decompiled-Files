/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class OidcTimeouts {
    private static final String CONNECT_TIMEOUT_PROPERTY_NAME = "com.atlassian.plugins.authentication.impl.web.oidc.connectTimeoutInMillis";
    private static final String READ_TIMEOUT_PROPERTY_NAME = "com.atlassian.plugins.authentication.impl.web.oidc.readTimeoutInMillis";
    private static final Logger log = LoggerFactory.getLogger(OidcTimeouts.class);
    private final int connectTimeoutInMillis = this.parseTimeout("com.atlassian.plugins.authentication.impl.web.oidc.connectTimeoutInMillis", 10000, "connect timeout");
    private final int readTimeoutInMillis = this.parseTimeout("com.atlassian.plugins.authentication.impl.web.oidc.readTimeoutInMillis", 120000, "read timeout");

    private int parseTimeout(String syspropName, int defaultValue, String friendlyPropertyName) {
        Integer valueFromSysprop = Integer.getInteger(syspropName);
        if (valueFromSysprop == null || valueFromSysprop < 0) {
            log.debug("Incorrect value '{}' declared for {}, falling back to default value '{}' milliseconds", new Object[]{valueFromSysprop, friendlyPropertyName, defaultValue});
            return defaultValue;
        }
        log.debug("Accepting custom value of '{}' milliseconds for {}", (Object)valueFromSysprop, (Object)friendlyPropertyName);
        return valueFromSysprop;
    }

    public int getConnectTimeoutInMillis() {
        return this.connectTimeoutInMillis;
    }

    public int getReadTimeoutInMillis() {
        return this.readTimeoutInMillis;
    }
}

