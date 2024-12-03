/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLinkRequest
 *  com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.sal.api.net.Response
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  net.oauth.OAuth$Parameter
 *  net.oauth.OAuthMessage
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.core.auth.AbstractApplicationLinkResponseHandler;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.sal.api.net.Response;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthRedirectingApplicationLinkResponseHandler
extends AbstractApplicationLinkResponseHandler {
    public static final String WWW_AUTH_HEADER = "WWW-Authenticate";
    private static final Logger log = LoggerFactory.getLogger(OAuthRedirectingApplicationLinkResponseHandler.class);
    protected static final Set<String> TOKEN_PROBLEMS = ImmutableSet.of((Object)"token_expired", (Object)"token_rejected", (Object)"token_revoked");
    protected final ConsumerTokenStoreService consumerTokenStoreService;
    protected final ApplicationId applicationId;
    protected final String username;
    protected boolean hasTokenProblems = false;
    protected String authenticationProblem = null;
    protected String authenticationProblemAdvice = null;
    protected List<OAuth.Parameter> allParameters;

    public OAuthRedirectingApplicationLinkResponseHandler(String url, ApplicationLinkRequest wrappedRequest, ConsumerTokenStoreService consumerTokenStoreService, ApplicationId applicationId, String username, boolean followRedirects) {
        super(url, wrappedRequest, followRedirects);
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.username = username;
        this.applicationId = applicationId;
    }

    protected void checkForOAuthProblemAndRemoveConsumerTokenIfNecessary(Response response) {
        String value = (String)response.getHeaders().get(WWW_AUTH_HEADER);
        if (!StringUtils.isBlank((CharSequence)value)) {
            this.allParameters = ImmutableList.copyOf((Collection)OAuthMessage.decodeAuthorization((String)value));
            for (OAuth.Parameter parameter : this.allParameters) {
                if ("oauth_problem".equals(parameter.getKey())) {
                    log.debug("OAuth request rejected by peer.\nOur OAuth request header: Authorization: " + this.wrappedRequest.getHeaders().get("Authorization") + "\nFull OAuth response header: WWW-Authenticate: " + value);
                    if ("timestamp_refused".equals(parameter.getValue())) {
                        log.warn("Peer rejected the timestamp on our OAuth request. This might be due to a replay attack, but it's more likely our system clock is not synchronized with the server's clock. You may turn on debug logging to log the full contents of the OAuth response headers.");
                    }
                    if (this.consumerTokenStoreService != null && TOKEN_PROBLEMS.contains(parameter.getValue())) {
                        try {
                            this.consumerTokenStoreService.removeConsumerToken(this.applicationId, this.username);
                        }
                        catch (RuntimeException e) {
                            log.error("Failed to delete consumer token for user '" + this.username + "'.", (Throwable)e);
                        }
                        this.hasTokenProblems = true;
                    }
                    this.authenticationProblem = parameter.getValue();
                }
                if (!"oauth_problem_advice".equals(parameter.getKey())) continue;
                this.authenticationProblemAdvice = parameter.getValue();
            }
        }
    }
}

