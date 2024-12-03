/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.auth.InternalOrphanedTrustDetector
 *  com.atlassian.applinks.core.auth.OrphanedTrustCertificate
 *  com.atlassian.applinks.core.auth.OrphanedTrustCertificate$Type
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.auth.InternalOrphanedTrustDetector;
import com.atlassian.applinks.core.auth.OrphanedTrustCertificate;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.oauth.auth.servlets.consumer.AddServiceProviderManuallyServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OAuthOrphanedTrustDetector
implements InternalOrphanedTrustDetector {
    private final ApplicationLinkService applicationLinkService;
    private final ServiceProviderConsumerStore serviceProviderConsumerStore;
    private final ServiceProviderStoreService serviceProviderStoreService;
    private final ConsumerService consumerService;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private static final Logger log = LoggerFactory.getLogger(OAuthOrphanedTrustDetector.class);

    @Autowired
    public OAuthOrphanedTrustDetector(ApplicationLinkService applicationLinkService, ServiceProviderConsumerStore serviceProviderConsumerStore, ServiceProviderStoreService serviceProviderStoreService, ConsumerService consumerService, AuthenticationConfigurationManager authenticationConfigurationManager) {
        this.applicationLinkService = applicationLinkService;
        this.serviceProviderConsumerStore = serviceProviderConsumerStore;
        this.serviceProviderStoreService = serviceProviderStoreService;
        this.consumerService = consumerService;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
    }

    public List<OrphanedTrustCertificate> findOrphanedTrustCertificates() {
        ArrayList<OrphanedTrustCertificate> orphanedTrustCertificates = new ArrayList<OrphanedTrustCertificate>();
        orphanedTrustCertificates.addAll(this.findOrphanedOAuthConsumers());
        orphanedTrustCertificates.addAll(this.findOrphanedOAuthServiceProviders());
        return orphanedTrustCertificates;
    }

    private List<OrphanedTrustCertificate> findOrphanedOAuthServiceProviders() {
        ArrayList<OrphanedTrustCertificate> orphanedTrustCertificates = new ArrayList<OrphanedTrustCertificate>();
        List<String> registeredServiceProviders = this.findRegisteredServiceProviders();
        Iterable allServiceProviders = this.consumerService.getAllServiceProviders();
        for (Consumer serviceProvider : allServiceProviders) {
            if (registeredServiceProviders.contains(serviceProvider.getKey())) continue;
            log.debug("Found orphaned Service Provider with consumer key '{}' and name '{}'", (Object)serviceProvider.getKey(), (Object)serviceProvider.getName());
            orphanedTrustCertificates.add(new OrphanedTrustCertificate(serviceProvider.getKey(), serviceProvider.getDescription(), OrphanedTrustCertificate.Type.OAUTH_SERVICE_PROVIDER));
        }
        return orphanedTrustCertificates;
    }

    private List<String> findRegisteredServiceProviders() {
        ArrayList<String> serviceProviderConsumerKeys = new ArrayList<String>();
        for (ApplicationLink link : this.applicationLinkService.getApplicationLinks()) {
            if (!this.authenticationConfigurationManager.isConfigured(link.getId(), OAuthAuthenticationProvider.class)) continue;
            Map configuration = this.authenticationConfigurationManager.getConfiguration(link.getId(), OAuthAuthenticationProvider.class);
            String consumerKey = (String)configuration.get(AddServiceProviderManuallyServlet.CONSUMER_KEY_OUTBOUND);
            serviceProviderConsumerKeys.add(consumerKey);
        }
        return serviceProviderConsumerKeys;
    }

    private List<OrphanedTrustCertificate> findOrphanedOAuthConsumers() {
        ArrayList<OrphanedTrustCertificate> orphanedTrustCertificates = new ArrayList<OrphanedTrustCertificate>();
        HashSet<String> recognisedConsumerKeys = new HashSet<String>();
        for (ApplicationLink link : this.applicationLinkService.getApplicationLinks()) {
            Consumer consumer = this.serviceProviderStoreService.getConsumer(link);
            if (consumer == null) continue;
            recognisedConsumerKeys.add(consumer.getKey());
        }
        for (Consumer consumer : this.serviceProviderConsumerStore.getAll()) {
            if (recognisedConsumerKeys.contains(consumer.getKey())) continue;
            orphanedTrustCertificates.add(new OrphanedTrustCertificate(consumer.getKey(), consumer.getDescription(), OrphanedTrustCertificate.Type.OAUTH));
        }
        return orphanedTrustCertificates;
    }

    public void deleteTrustCertificate(String id, OrphanedTrustCertificate.Type type) {
        this.checkCertificateType(type);
        if (type == OrphanedTrustCertificate.Type.OAUTH) {
            this.serviceProviderConsumerStore.remove(id);
        } else if (type == OrphanedTrustCertificate.Type.OAUTH_SERVICE_PROVIDER) {
            this.consumerService.removeConsumerByKey(id);
        }
    }

    private void checkCertificateType(OrphanedTrustCertificate.Type type) {
        if (!this.canHandleCertificateType(type)) {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    public boolean canHandleCertificateType(OrphanedTrustCertificate.Type type) {
        return type == OrphanedTrustCertificate.Type.OAUTH || type == OrphanedTrustCertificate.Type.OAUTH_SERVICE_PROVIDER;
    }

    public void addOrphanedTrustToApplicationLink(String id, OrphanedTrustCertificate.Type type, ApplicationId applicationId) {
        ApplicationLink applicationLink;
        this.checkCertificateType(type);
        try {
            applicationLink = this.applicationLinkService.getApplicationLink(applicationId);
            if (applicationLink == null) {
                throw new NoApplicationIdFoundException("No Application Link with id '" + applicationId + "' found.");
            }
        }
        catch (TypeNotInstalledException e) {
            throw new IllegalStateException("An application of the type " + e.getType() + " is not installed!", e);
        }
        if (type == OrphanedTrustCertificate.Type.OAUTH) {
            this.registerOAuthConsumer(id, applicationLink);
        } else if (type == OrphanedTrustCertificate.Type.OAUTH_SERVICE_PROVIDER) {
            this.registerOAuthServiceProvider(id, applicationLink);
        }
    }

    private void registerOAuthServiceProvider(String id, ApplicationLink applicationLink) {
        Consumer consumer = this.consumerService.getConsumerByKey(id);
        String requestTokenUrl = applicationLink.getRpcUrl() + "/request/token";
        String accessTokenUrl = applicationLink.getRpcUrl() + "/access/token";
        String authorizeUrl = applicationLink.getDisplayUrl() + "/authorize/token";
        this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), OAuthAuthenticationProvider.class, (Map)ImmutableMap.of((Object)AddServiceProviderManuallyServlet.CONSUMER_KEY_OUTBOUND, (Object)consumer.getKey(), (Object)AddServiceProviderManuallyServlet.SERVICE_PROVIDER_REQUEST_TOKEN_URL, (Object)requestTokenUrl, (Object)AddServiceProviderManuallyServlet.SERVICE_PROVIDER_ACCESS_TOKEN_URL, (Object)accessTokenUrl, (Object)AddServiceProviderManuallyServlet.SERVICE_PROVIDER_AUTHORIZE_URL, (Object)authorizeUrl));
        log.debug("Associated OAuth ServiceProvider with consumer key '{}' with Application Link id='{}' and name='{}'", new Object[]{consumer.getKey(), applicationLink.getId(), applicationLink.getName()});
    }

    private void registerOAuthConsumer(String id, ApplicationLink applicationLink) {
        Consumer consumer = this.serviceProviderConsumerStore.get(id);
        if (consumer == null) {
            throw new NoConsumerFoundException("No consumer with id '" + id + "' registered!");
        }
        this.serviceProviderStoreService.addConsumer(consumer, applicationLink);
        log.debug("Associated OAuth Consumer with key '{}' with Application Link id='{}' and name='{}'", new Object[]{consumer.getKey(), applicationLink.getId(), applicationLink.getName()});
    }

    public static class NoConsumerFoundException
    extends RuntimeException {
        public NoConsumerFoundException(String message) {
            super(message);
        }
    }

    public static class NoApplicationIdFoundException
    extends RuntimeException {
        public NoApplicationIdFoundException(String message) {
            super(message);
        }
    }
}

