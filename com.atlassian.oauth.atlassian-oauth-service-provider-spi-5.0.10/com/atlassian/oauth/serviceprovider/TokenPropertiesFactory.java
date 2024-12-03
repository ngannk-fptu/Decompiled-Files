/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Request
 */
package com.atlassian.oauth.serviceprovider;

import com.atlassian.oauth.Request;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import java.util.Map;

public interface TokenPropertiesFactory {
    public static final String ALTERNAME_CONSUMER_NAME = "alternate.consumer.name";

    public Map<String, String> newRequestTokenProperties(Request var1);

    public Map<String, String> newAccessTokenProperties(ServiceProviderToken var1);
}

