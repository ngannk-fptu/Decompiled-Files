/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.core.spi.AuditMethods
 *  com.atlassian.audit.core.spi.service.AuditMethodProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.core.spi.AuditMethods;
import com.atlassian.audit.core.spi.service.AuditMethodProvider;
import com.atlassian.confluence.impl.security.delegate.ScopesRequestCacheDelegate;
import com.atlassian.confluence.util.RequestCacheThreadLocal;
import java.util.Optional;
import javax.annotation.Nonnull;

public class ConfluenceAuditMethodProvider
implements AuditMethodProvider {
    private static final String OAUTH_METHOD = "OAuth";
    private final ScopesRequestCacheDelegate scopesRequestCacheDelegate;

    public ConfluenceAuditMethodProvider(ScopesRequestCacheDelegate scopesRequestCacheDelegate) {
        this.scopesRequestCacheDelegate = scopesRequestCacheDelegate;
    }

    @Nonnull
    public String currentMethod() {
        boolean isMobileAppRequest = this.isMobileAppRequest();
        Optional<String> oauth2ApplicationName = this.scopesRequestCacheDelegate.getApplicationNameForRequest();
        if (oauth2ApplicationName.isPresent()) {
            return this.getOAuthMethod(isMobileAppRequest, oauth2ApplicationName.get());
        }
        String remoteAddress = RequestCacheThreadLocal.getRemoteAddress();
        if (remoteAddress == null) {
            return AuditMethods.system();
        }
        if (this.isMobileAppRequest()) {
            return AuditMethods.mobile();
        }
        return AuditMethods.browser();
    }

    private String getOAuthMethod(boolean isMobileAppRequest, String oauth2ApplicationName) {
        String applicationName = isMobileAppRequest ? AuditMethods.mobile() : oauth2ApplicationName;
        return applicationName + " - OAuth";
    }

    private boolean isMobileAppRequest() {
        return RequestCacheThreadLocal.getMobileAppRequestHeader() != null;
    }
}

