/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerInformationHelper
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.Request$Parameter
 *  com.atlassian.sal.api.net.ResponseException
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  net.oauth.OAuth$Parameter
 *  net.oauth.OAuthMessage
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerInformationHelper;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;

public class OAuthHelper {
    private static final Function<Map.Entry<String, String>, Request.Parameter> toRequestParameters = new Function<Map.Entry<String, String>, Request.Parameter>(){

        public Request.Parameter apply(Map.Entry<String, String> p) {
            Objects.requireNonNull(p, "parameter");
            return new Request.Parameter(p.getKey(), p.getValue());
        }
    };
    private static final Function<Request.Parameter, OAuth.Parameter> toOAuthParameters = new Function<Request.Parameter, OAuth.Parameter>(){

        public OAuth.Parameter apply(Request.Parameter p) {
            Objects.requireNonNull(p, "parameter");
            return new OAuth.Parameter(p.getName(), p.getValue());
        }
    };

    private OAuthHelper() {
    }

    public static boolean isOAuthPluginInstalled(ApplicationLink applicationLink) {
        boolean oAuthPluginInstalled = false;
        try {
            Consumer consumer = OAuthHelper.fetchConsumerInformation(applicationLink);
            return consumer.getKey() != null;
        }
        catch (ResponseException responseException) {
            return false;
        }
    }

    @Deprecated
    public static Consumer fetchConsumerInformation(ApplicationLink applicationLink) throws ResponseException {
        return ConsumerInformationHelper.fetchConsumerInformation((ApplicationLink)applicationLink);
    }

    public static OAuthMessage asOAuthMessage(Request request) {
        Objects.requireNonNull(request, "request");
        return new OAuthMessage(request.getMethod().name(), request.getUri().toString(), (Collection)ImmutableList.copyOf(OAuthHelper.asOAuthParameters(request.getParameters())));
    }

    public static Iterable<OAuth.Parameter> asOAuthParameters(Iterable<Request.Parameter> requestParameters) {
        Objects.requireNonNull(requestParameters, "requestParameters");
        return Iterables.transform(requestParameters, toOAuthParameters);
    }

    public static Iterable<Request.Parameter> fromOAuthParameters(List<? extends Map.Entry<String, String>> oauthParameters) {
        Objects.requireNonNull(oauthParameters, "oauthParameters");
        return Iterables.transform(oauthParameters, toRequestParameters);
    }
}

