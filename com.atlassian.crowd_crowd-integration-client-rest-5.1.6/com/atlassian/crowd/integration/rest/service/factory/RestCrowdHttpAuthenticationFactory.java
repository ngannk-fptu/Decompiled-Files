/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.integration.http.CrowdHttpAuthenticator
 *  com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl
 *  com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper
 *  com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl
 *  com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor
 *  com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl
 *  com.atlassian.crowd.service.client.ClientProperties
 *  com.atlassian.crowd.service.client.ClientPropertiesImpl
 *  com.atlassian.crowd.service.client.ClientResourceLocator
 *  com.atlassian.crowd.service.client.CrowdClient
 *  com.atlassian.crowd.service.client.ResourceLocator
 */
package com.atlassian.crowd.integration.rest.service.factory;

import com.atlassian.crowd.integration.http.CrowdHttpAuthenticator;
import com.atlassian.crowd.integration.http.CrowdHttpAuthenticatorImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelper;
import com.atlassian.crowd.integration.http.util.CrowdHttpTokenHelperImpl;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor;
import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractorImpl;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.ClientResourceLocator;
import com.atlassian.crowd.service.client.CrowdClient;
import com.atlassian.crowd.service.client.ResourceLocator;

public class RestCrowdHttpAuthenticationFactory {
    private static final CrowdHttpAuthenticator crowdHttpAuthenticator = RestCrowdHttpAuthenticationFactory.createInstance();

    private RestCrowdHttpAuthenticationFactory() {
    }

    public static CrowdHttpAuthenticator getAuthenticator() {
        return crowdHttpAuthenticator;
    }

    private static CrowdHttpAuthenticator createInstance() {
        ClientResourceLocator clientResourceLocator = new ClientResourceLocator("crowd.properties");
        ClientPropertiesImpl clientProperties = ClientPropertiesImpl.newInstanceFromResourceLocator((ResourceLocator)clientResourceLocator);
        RestCrowdClientFactory clientFactory = new RestCrowdClientFactory();
        CrowdClient crowdClient = clientFactory.newInstance((ClientProperties)clientProperties);
        CrowdHttpTokenHelper tokenHelper = CrowdHttpTokenHelperImpl.getInstance((CrowdHttpValidationFactorExtractor)CrowdHttpValidationFactorExtractorImpl.getInstance());
        return new CrowdHttpAuthenticatorImpl(crowdClient, (ClientProperties)clientProperties, tokenHelper);
    }
}

