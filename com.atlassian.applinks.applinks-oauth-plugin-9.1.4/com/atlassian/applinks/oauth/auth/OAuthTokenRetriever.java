/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.core.util.Holder
 *  com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.Request$HttpMethod
 *  com.atlassian.oauth.Request$Parameter
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.oauth.consumer.ConsumerToken$ConsumerTokenBuilder
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ResponseHandler
 *  net.oauth.OAuth
 *  net.oauth.OAuth$Parameter
 *  net.oauth.OAuthMessage
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.core.util.Holder;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException;
import com.atlassian.applinks.oauth.auth.OAuthParameters;
import com.atlassian.applinks.oauth.auth.OAuthPermissionDeniedException;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.Request;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class OAuthTokenRetriever {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthTokenRetriever.class);
    private ConsumerService consumerService;
    private RequestFactory requestFactory;

    @Autowired
    public OAuthTokenRetriever(ConsumerService consumerService, RequestFactory requestFactory) {
        this.consumerService = consumerService;
        this.requestFactory = requestFactory;
    }

    public ConsumerToken getRequestToken(ServiceProvider serviceProvider, String consumerKey, String callback) throws ResponseException {
        com.atlassian.oauth.Request oAuthRequest = new com.atlassian.oauth.Request(Request.HttpMethod.POST, serviceProvider.getRequestTokenUri(), Collections.singleton(new Request.Parameter("oauth_callback", callback)));
        com.atlassian.oauth.Request signedRequest = this.consumerService.sign(oAuthRequest, consumerKey, serviceProvider);
        Request tokenRequest = this.requestFactory.createRequest(Request.MethodType.POST, serviceProvider.getRequestTokenUri().toString());
        tokenRequest.addRequestParameters(this.parameterToStringArray(signedRequest.getParameters()));
        TokenAndSecret tokenAndSecret = this.requestToken(serviceProvider.getRequestTokenUri().toString(), signedRequest);
        ConsumerToken requestToken = ((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)ConsumerToken.newRequestToken((String)tokenAndSecret.token).tokenSecret(tokenAndSecret.secret)).consumer(this.getConsumer(consumerKey))).build();
        assert (requestToken.isRequestToken());
        return requestToken;
    }

    public ConsumerToken getAccessToken(ServiceProvider serviceProvider, ConsumerToken requestTokenPair, String requestVerifier, String consumerKey) throws ResponseException {
        ArrayList<Request.Parameter> parameters = new ArrayList<Request.Parameter>();
        parameters.add(new Request.Parameter("oauth_token", requestTokenPair.getToken()));
        if (StringUtils.isNotBlank((CharSequence)requestVerifier)) {
            parameters.add(new Request.Parameter("oauth_verifier", requestVerifier));
        }
        com.atlassian.oauth.Request oAuthRequest = new com.atlassian.oauth.Request(Request.HttpMethod.POST, serviceProvider.getAccessTokenUri(), parameters);
        com.atlassian.oauth.Request signedRequest = this.consumerService.sign(oAuthRequest, serviceProvider, requestTokenPair);
        TokenAndSecret tokenAndSecret = this.requestToken(serviceProvider.getAccessTokenUri().toString(), signedRequest);
        ConsumerToken accessToken = ((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)ConsumerToken.newAccessToken((String)tokenAndSecret.token).tokenSecret(tokenAndSecret.secret)).consumer(this.getConsumer(consumerKey))).build();
        assert (accessToken.isAccessToken());
        return accessToken;
    }

    private Consumer getConsumer(String consumerKey) {
        return this.consumerService.getConsumerByKey(consumerKey) == null ? this.consumerService.getConsumer() : this.consumerService.getConsumerByKey(consumerKey);
    }

    private TokenAndSecret requestToken(String url, com.atlassian.oauth.Request signedRequest) throws ResponseException {
        Request tokenRequest = this.requestFactory.createRequest(Request.MethodType.POST, url);
        tokenRequest.addRequestParameters(this.parameterToStringArray(signedRequest.getParameters()));
        final Holder oauthParametersHolder = new Holder();
        final Holder responseHolder = new Holder();
        ResponseHandler<Response> responseHandler = new ResponseHandler<Response>(){

            public void handle(Response response) throws ResponseException {
                responseHolder.set((Object)response);
                if (response.isSuccessful()) {
                    try {
                        List parameters = OAuth.decodeForm((String)response.getResponseBodyAsString());
                        Map map = OAuth.newMap((Iterable)parameters);
                        oauthParametersHolder.set((Object)map);
                    }
                    catch (Exception e) {
                        throw new ResponseException("Failed to get token from service provider. Couldn't parse response body " + response.getResponseBodyAsString() + "'", (Throwable)e);
                    }
                } else {
                    String authHeader = response.getHeader("WWW-Authenticate");
                    if (authHeader != null && authHeader.startsWith("OAuth")) {
                        List parameters = OAuthMessage.decodeAuthorization((String)authHeader);
                        String problem = "";
                        for (OAuth.Parameter parameter : parameters) {
                            if (!parameter.getKey().equals("oauth_problem")) continue;
                            problem = parameter.getValue();
                        }
                        if ("permission_denied".equals(problem)) {
                            throw new OAuthPermissionDeniedException("User refused to permit this consumer to access protected resources, full details: " + authHeader);
                        }
                        throw new OAuthMessageProblemException("Failed to get token from service provider, problem was: '" + problem + "'", OAuthParameters.asMap(parameters));
                    }
                    throw new ResponseException("Failed to get token from service provider. Response status code is '" + response.getStatusCode() + "'");
                }
            }
        };
        tokenRequest.setFollowRedirects(false);
        tokenRequest.execute((ResponseHandler)responseHandler);
        Map oAuthParameterMap = (Map)oauthParametersHolder.get();
        String secret = (String)oAuthParameterMap.get("oauth_token_secret");
        if (StringUtils.isEmpty((CharSequence)secret)) {
            String msg = "Failed to get token from service provider. Secret is missing in response.";
            this.logResponseContent((Holder<Response>)responseHolder, msg);
            throw new ResponseException(msg);
        }
        String token = (String)oAuthParameterMap.get("oauth_token");
        if (StringUtils.isEmpty((CharSequence)token)) {
            String msg = "Failed to get token from service provider. Token is missing in response.";
            this.logResponseContent((Holder<Response>)responseHolder, msg);
            throw new ResponseException(msg);
        }
        TokenAndSecret tokenAndSecret = new TokenAndSecret();
        tokenAndSecret.secret = secret;
        tokenAndSecret.token = token;
        return tokenAndSecret;
    }

    private void logResponseContent(Holder<Response> responseHolder, String msg) throws ResponseException {
        if (LOG.isDebugEnabled() && responseHolder.get() != null) {
            if (((Response)responseHolder.get()).getHeaders() != null) {
                msg = msg + "\nresponse headers:" + ((Response)responseHolder.get()).getHeaders();
            }
            if (((Response)responseHolder.get()).getResponseBodyAsString() != null) {
                msg = msg + "\nresponse message:" + ((Response)responseHolder.get()).getResponseBodyAsString();
            }
            msg = msg + "\n";
            LOG.debug(msg);
        }
    }

    private String[] parameterToStringArray(Iterable<Request.Parameter> iterable) {
        ArrayList<String> list = new ArrayList<String>();
        for (Request.Parameter parameter : iterable) {
            list.add(parameter.getName());
            list.add(parameter.getValue());
        }
        return list.toArray(new String[0]);
    }

    private class TokenAndSecret {
        public String token;
        public String secret;

        private TokenAndSecret() {
        }
    }
}

