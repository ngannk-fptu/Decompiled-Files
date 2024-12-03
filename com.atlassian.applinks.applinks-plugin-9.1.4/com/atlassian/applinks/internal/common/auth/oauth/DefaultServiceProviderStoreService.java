/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultServiceProviderStoreService
implements ServiceProviderStoreService {
    private final ServiceProviderConsumerStore serviceProviderConsumerStore;
    private final ServiceProviderTokenStore serviceProviderTokenStore;

    @Autowired
    public DefaultServiceProviderStoreService(ServiceProviderConsumerStore serviceProviderConsumerStore, ServiceProviderTokenStore serviceProviderTokenStore) {
        this.serviceProviderConsumerStore = serviceProviderConsumerStore;
        this.serviceProviderTokenStore = serviceProviderTokenStore;
    }

    @Override
    public void addConsumer(Consumer consumer, ApplicationLink applicationLink) {
        this.serviceProviderConsumerStore.put(consumer);
        applicationLink.putProperty(ApplinksOAuth.PROPERTY_INCOMING_CONSUMER_KEY, (Object)consumer.getKey());
    }

    private String getConsumerKey(ApplicationLink applicationLink) {
        Object storedConsumerKey = applicationLink.getProperty(ApplinksOAuth.PROPERTY_INCOMING_CONSUMER_KEY);
        if (storedConsumerKey != null) {
            return storedConsumerKey.toString();
        }
        return null;
    }

    @Override
    public void removeConsumer(ApplicationLink applicationLink) {
        String consumerKey = this.getConsumerKey(applicationLink);
        if (consumerKey == null) {
            throw new IllegalStateException("No consumer configured for application link '" + applicationLink + "'.");
        }
        this.serviceProviderTokenStore.removeByConsumer(consumerKey);
        this.serviceProviderConsumerStore.remove(consumerKey);
        if (applicationLink.removeProperty(ApplinksOAuth.PROPERTY_INCOMING_CONSUMER_KEY) == null) {
            throw new IllegalStateException("Failed to remove consumer with key '" + consumerKey + "' from application link '" + applicationLink + "'");
        }
    }

    @Override
    public Consumer getConsumer(ApplicationLink applicationLink) {
        String consumerKey = this.getConsumerKey(applicationLink);
        if (consumerKey != null) {
            return this.serviceProviderConsumerStore.get(consumerKey);
        }
        return null;
    }
}

