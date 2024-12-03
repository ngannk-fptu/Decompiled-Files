/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Singleton
 *  org.apache.shindig.gadgets.http.HttpRequest
 *  org.apache.shindig.gadgets.oauth.OAuthCallbackGenerator
 *  org.apache.shindig.gadgets.oauth.OAuthFetcherConfig
 *  org.apache.shindig.gadgets.oauth.OAuthResponseParams
 *  org.apache.shindig.gadgets.oauth.OAuthResponseParams$OAuthRequestException
 */
package com.atlassian.gadgets.renderer.internal;

import com.google.inject.Singleton;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.oauth.OAuthCallbackGenerator;
import org.apache.shindig.gadgets.oauth.OAuthFetcherConfig;
import org.apache.shindig.gadgets.oauth.OAuthResponseParams;

@Singleton
public class AtlassianOAuthCallbackGenerator
implements OAuthCallbackGenerator {
    public String generateCallback(OAuthFetcherConfig fetcherConfig, String baseCallback, HttpRequest request, OAuthResponseParams responseParams) throws OAuthResponseParams.OAuthRequestException {
        return baseCallback;
    }
}

