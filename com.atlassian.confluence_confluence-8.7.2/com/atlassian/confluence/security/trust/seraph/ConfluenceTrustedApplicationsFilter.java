/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.atlassian.security.auth.trustedapps.UserResolver
 *  com.atlassian.security.auth.trustedapps.filter.AuthenticationController
 *  com.atlassian.security.auth.trustedapps.filter.AuthenticationListener
 *  com.atlassian.security.auth.trustedapps.filter.TrustedApplicationsFilter
 *  com.atlassian.security.auth.trustedapps.seraph.filter.SeraphAuthenticationController
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.LazyReference$InitializationException
 *  javax.servlet.Filter
 *  javax.servlet.ServletException
 */
package com.atlassian.confluence.security.trust.seraph;

import com.atlassian.confluence.security.trust.seraph.ConfluenceSeraphAuthenticationListener;
import com.atlassian.confluence.util.AbstractBootstrapHotSwappingFilter;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.security.auth.trustedapps.UserResolver;
import com.atlassian.security.auth.trustedapps.filter.AuthenticationController;
import com.atlassian.security.auth.trustedapps.filter.AuthenticationListener;
import com.atlassian.security.auth.trustedapps.filter.TrustedApplicationsFilter;
import com.atlassian.security.auth.trustedapps.seraph.filter.SeraphAuthenticationController;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.LazyReference;
import javax.servlet.Filter;
import javax.servlet.ServletException;

public class ConfluenceTrustedApplicationsFilter
extends AbstractBootstrapHotSwappingFilter {
    private final LazyReference<TrustedApplicationsManager> trustedApplicationsReference = new LazyComponentReference("seraphTrustedApplicationIntegration");
    private final LazyReference<UserResolver> userResolverReference = new LazyComponentReference("seraphTrustedApplicationUserResolver");

    @Override
    public Filter getSwapTarget() throws ServletException {
        if (!ContainerManager.isContainerSetup()) {
            throw new ServletException("Container is not set up yet.");
        }
        try {
            TrustedApplicationsManager applicationsManager = (TrustedApplicationsManager)this.trustedApplicationsReference.get();
            UserResolver userResolver = (UserResolver)this.userResolverReference.get();
            SeraphAuthenticationController authenticationController = new SeraphAuthenticationController(SecurityConfigFactory.getInstance().getRoleMapper());
            return new TrustedApplicationsFilter(applicationsManager, userResolver, (AuthenticationController)authenticationController, (AuthenticationListener)new ConfluenceSeraphAuthenticationListener());
        }
        catch (LazyReference.InitializationException | IllegalStateException e) {
            throw new ServletException("Could not get components from application context", e);
        }
    }
}

