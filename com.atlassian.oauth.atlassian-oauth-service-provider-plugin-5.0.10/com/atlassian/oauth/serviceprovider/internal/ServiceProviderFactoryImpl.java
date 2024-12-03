/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.serviceprovider.internal.ServiceProviderFactory;
import com.atlassian.sal.api.ApplicationProperties;
import java.net.URI;
import java.util.Objects;

public class ServiceProviderFactoryImpl
implements ServiceProviderFactory {
    private final ApplicationProperties applicationProperties;

    public ServiceProviderFactoryImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
    }

    @Override
    public ServiceProvider newServiceProvider() {
        return new ServiceProvider(URI.create(this.applicationProperties.getBaseUrl() + "/plugins/servlet/oauth/request-token"), URI.create(this.applicationProperties.getBaseUrl() + "/plugins/servlet/oauth/authorize"), URI.create(this.applicationProperties.getBaseUrl() + "/plugins/servlet/oauth/access-token"));
    }
}

