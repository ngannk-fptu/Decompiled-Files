/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.event.BeforeApplicationLinkDeletedEvent
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.event.BeforeApplicationLinkDeletedEvent;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.oauth.auth.servlets.consumer.AddServiceProviderManuallyServlet;
import com.atlassian.applinks.oauth.auth.servlets.serviceprovider.AbstractConsumerServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class OAuthConfigListener
implements DisposableBean {
    private final EventPublisher eventPublisher;
    private final ServiceProviderTokenStore serviceProviderTokenStore;
    private final ServiceProviderConsumerStore serviceProviderConsumerStore;
    private final AuthenticationConfigurationManager configurationManager;
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final ConsumerService consumerService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OAuthConfigListener(EventPublisher eventPublisher, ServiceProviderTokenStore serviceProviderTokenStore, ServiceProviderConsumerStore serviceProviderConsumerStore, AuthenticationConfigurationManager configurationManager, ConsumerTokenStoreService consumerTokenStoreService, ConsumerService consumerService) {
        this.eventPublisher = eventPublisher;
        this.serviceProviderTokenStore = serviceProviderTokenStore;
        this.serviceProviderConsumerStore = serviceProviderConsumerStore;
        this.configurationManager = configurationManager;
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.consumerService = consumerService;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void onApplicationLinkDeleted(BeforeApplicationLinkDeletedEvent beforeApplicationLinkDeletedEvent) {
        ApplicationLink applicationLink = beforeApplicationLinkDeletedEvent.getApplicationLink();
        Object oConsumerKey = applicationLink.getProperty(AbstractConsumerServlet.OAUTH_INCOMING_CONSUMER_KEY);
        if (oConsumerKey != null) {
            String consumerKey = oConsumerKey.toString();
            this.serviceProviderTokenStore.removeByConsumer(consumerKey);
            this.serviceProviderConsumerStore.remove(consumerKey);
            this.logger.debug("Unregistered consumer with key '{}' for deleted application link {}", oConsumerKey, (Object)applicationLink);
        }
        if (this.configurationManager.isConfigured(applicationLink.getId(), OAuthAuthenticationProvider.class)) {
            Map configuration = this.configurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
            String consumerKey = (String)configuration.get(AddServiceProviderManuallyServlet.CONSUMER_KEY_OUTBOUND);
            if (!StringUtils.isEmpty((CharSequence)consumerKey)) {
                this.consumerService.removeConsumerByKey(consumerKey);
                this.logger.debug("Unregistered service provider with consumer key '{}' for deleted application link {}", (Object)consumerKey, (Object)applicationLink);
            }
            this.consumerTokenStoreService.removeAllConsumerTokens(applicationLink);
            this.logger.debug("Removed token for deleted application link {}", (Object)applicationLink);
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

