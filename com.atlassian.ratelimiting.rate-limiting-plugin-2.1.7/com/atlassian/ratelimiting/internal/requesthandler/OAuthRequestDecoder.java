/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Streams
 *  javax.servlet.http.HttpServletRequest
 *  net.oauth.OAuth$Parameter
 *  net.oauth.OAuthMessage
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.requesthandler;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.ratelimiting.requesthandler.PreAuthRequestSingleMethodDecoder;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Streams;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthRequestDecoder
implements PreAuthRequestSingleMethodDecoder {
    private static final Logger logger = LoggerFactory.getLogger(OAuthRequestDecoder.class);
    private static final String OAUTH_TOKEN_PARAMETER = "oauth_token";
    private final UserKeyProvider userKeyProvider;
    private final ServiceProviderTokenStore tokenStore;

    public OAuthRequestDecoder(ServiceProviderTokenStore tokenStore, UserKeyProvider userKeyProvider) {
        this.tokenStore = tokenStore;
        this.userKeyProvider = userKeyProvider;
    }

    @Override
    public Optional<UserKey> getUserKey(HttpServletRequest httpServletRequest) {
        Optional<UserKey> basicAuthUser = this.getOAuthUserKey(httpServletRequest);
        basicAuthUser.ifPresent(userKey -> logger.trace("Pre-auth user detected from OAuth: {}", userKey));
        return basicAuthUser;
    }

    private Optional<UserKey> getOAuthUserKey(HttpServletRequest httpServletRequest) {
        return Streams.concat((Stream[])new Stream[]{this.getTokensFromRequestHeader(httpServletRequest), this.getTokensFromRequestParameter(httpServletRequest)}).map(arg_0 -> ((ServiceProviderTokenStore)this.tokenStore).get(arg_0)).filter(Objects::nonNull).map(ServiceProviderToken::getUser).findFirst().map(Principal::getName).flatMap(this.userKeyProvider);
    }

    private Stream<String> getTokensFromRequestParameter(HttpServletRequest httpServletRequest) {
        return Arrays.stream((Object[])MoreObjects.firstNonNull((Object)httpServletRequest.getParameterValues(OAUTH_TOKEN_PARAMETER), (Object)new String[0]));
    }

    private Stream<String> getTokensFromRequestHeader(HttpServletRequest httpServletRequest) {
        return Collections.list((Enumeration)MoreObjects.firstNonNull((Object)httpServletRequest.getHeaders("Authorization"), Collections.emptyEnumeration())).stream().map(OAuthMessage::decodeAuthorization).flatMap(Collection::stream).filter(p -> OAUTH_TOKEN_PARAMETER.equals(p.getKey())).map(OAuth.Parameter::getValue);
    }
}

