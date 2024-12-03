/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.bridge.Consumers
 *  com.atlassian.oauth.bridge.serviceprovider.ServiceProviderTokens
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  net.oauth.OAuthAccessor
 *  net.oauth.OAuthConsumer
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.bridge.Consumers;
import com.atlassian.oauth.bridge.serviceprovider.ServiceProviderTokens;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.internal.ServiceProviderFactory;
import java.util.Objects;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;

public class OAuthConverter {
    private final ServiceProviderFactory serviceProviderFactory;

    public OAuthConverter(ServiceProviderFactory serviceProviderFactory) {
        this.serviceProviderFactory = Objects.requireNonNull(serviceProviderFactory, "serviceProviderFactor");
    }

    public OAuthAccessor toOAuthAccessor(ServiceProviderToken token) {
        return ServiceProviderTokens.asOAuthAccessor((ServiceProviderToken)token, (ServiceProvider)this.serviceProviderFactory.newServiceProvider());
    }

    public OAuthConsumer toOAuthConsumer(Consumer consumer) {
        return Consumers.asOAuthConsumer((Consumer)consumer, (ServiceProvider)this.serviceProviderFactory.newServiceProvider());
    }
}

