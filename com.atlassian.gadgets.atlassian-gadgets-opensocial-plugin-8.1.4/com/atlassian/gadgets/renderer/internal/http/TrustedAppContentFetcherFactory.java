/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.security.auth.trustedapps.EncryptedCertificate
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationUtils
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.shindig.gadgets.GadgetException
 *  org.apache.shindig.gadgets.http.ContentFetcherFactory
 *  org.apache.shindig.gadgets.http.HttpRequest
 *  org.apache.shindig.gadgets.http.HttpResponse
 *  org.apache.shindig.gadgets.http.RemoteContentFetcherFactory
 *  org.apache.shindig.gadgets.oauth.OAuthFetcherFactory
 */
package com.atlassian.gadgets.renderer.internal.http;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.http.ContentFetcherFactory;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponse;
import org.apache.shindig.gadgets.http.RemoteContentFetcherFactory;
import org.apache.shindig.gadgets.oauth.OAuthFetcherFactory;

@Singleton
public class TrustedAppContentFetcherFactory
extends ContentFetcherFactory {
    private final TrustedApplicationsManager trustedAppsManager;
    private final UserManager userManager;

    @Inject
    public TrustedAppContentFetcherFactory(RemoteContentFetcherFactory remoteContentFetcherFactory, OAuthFetcherFactory oauthFetcherFactory, @ComponentImport TrustedApplicationsManager trustedAppsManager, @ComponentImport UserManager userManager) {
        super(remoteContentFetcherFactory, oauthFetcherFactory);
        this.trustedAppsManager = trustedAppsManager;
        this.userManager = userManager;
    }

    public HttpResponse fetch(HttpRequest request) throws GadgetException {
        this.addTrustedAppHeaders(request, this.userManager.getRemoteUsername());
        return super.fetch(request);
    }

    private void addTrustedAppHeaders(HttpRequest request, String username) {
        EncryptedCertificate userCertificate = this.createCertificate(username, request.getUri().toString());
        if (userCertificate != null && !StringUtils.isBlank((CharSequence)userCertificate.getID())) {
            TrustedApplicationUtils.addRequestParameters((EncryptedCertificate)userCertificate, (arg_0, arg_1) -> ((HttpRequest)request).addHeader(arg_0, arg_1));
        }
    }

    private EncryptedCertificate createCertificate(String username, String url) {
        if (StringUtils.isNotBlank((CharSequence)username) && StringUtils.isNotBlank((CharSequence)url)) {
            return this.trustedAppsManager.getCurrentApplication().encode(username, url);
        }
        return null;
    }
}

