/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestError
 *  com.atlassian.oauth2.client.api.storage.config.ProviderType
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.client.lib.web;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestError;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.atlassian.oauth2.client.lib.flow.FlowRequestData;
import com.atlassian.oauth2.client.lib.flow.FlowRequestErrorImpl;
import com.atlassian.oauth2.client.lib.flow.ServletFlowRequestService;
import com.atlassian.oauth2.client.lib.token.InternalTokenService;
import com.atlassian.oauth2.client.lib.web.AuthorizationCodeFlowUrlsProvider;
import com.atlassian.oauth2.client.properties.SystemProperty;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.nimbusds.oauth2.sdk.AuthorizationErrorResponse;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.AuthorizationResponse;
import com.nimbusds.oauth2.sdk.AuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.Prompt;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationCodeFlowServlet
extends HttpServlet {
    private static final long serialVersionUID = -3739803064653415056L;
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCodeFlowServlet.class);
    private final ServletFlowRequestService servletFlowRequestService;
    private final InternalTokenService tokenService;
    private final AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider;

    public AuthorizationCodeFlowServlet(ServletFlowRequestService servletFlowRequestService, InternalTokenService tokenService, AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider) {
        this.servletFlowRequestService = servletFlowRequestService;
        this.tokenService = tokenService;
        this.authorizationCodeFlowUrlsProvider = authorizationCodeFlowUrlsProvider;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String flowId = request.getParameter("startFlow");
        if (!Strings.isNullOrEmpty((String)flowId)) {
            this.startFlow(request, response, flowId);
        } else {
            this.exchangeAuthorizationCode(request, response);
        }
    }

    private void exchangeAuthorizationCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        FlowRequestData flowData;
        AuthorizationResponse authorizationResponse = this.parseResponse(request);
        if (authorizationResponse != null && (flowData = this.tryFetchFlowRequestData(request, authorizationResponse)) != null) {
            this.handleAuthorizationResponse(request, authorizationResponse, flowData);
            response.sendRedirect(flowData.getClientRedirectUrl());
            return;
        }
        this.sendRedirectToBaseUrl(response);
    }

    private void handleAuthorizationResponse(HttpServletRequest request, AuthorizationResponse authorizationResponse, FlowRequestData flowData) {
        try {
            if (authorizationResponse.indicatesSuccess()) {
                AuthorizationSuccessResponse successResponse = authorizationResponse.toSuccessResponse();
                this.authorizationCodeFlowUrlsProvider.validateRedirectUri(flowData.getClientConfiguration(), request);
                logger.debug("Exchanging an authorization code for tokens for a client with an ID [{}]", (Object)flowData.getClientConfiguration().getClientId());
                ClientToken token = this.tokenService.getAccessTokenFromAuthorizationCode(flowData.getClientConfiguration(), successResponse.getAuthorizationCode().getValue());
                this.servletFlowRequestService.updateFlowRequest(request.getSession(false), flowData, token);
            } else {
                AuthorizationErrorResponse errorResponse = authorizationResponse.toErrorResponse();
                this.servletFlowRequestService.updateFlowRequest(request.getSession(false), flowData, (FlowRequestError)new FlowRequestErrorImpl("Error when fetching authorization response: " + errorResponse.getErrorObject().toJSONObject().toString()));
            }
        }
        catch (Exception e) {
            this.servletFlowRequestService.updateFlowRequest(request.getSession(false), flowData, (FlowRequestError)new FlowRequestErrorImpl(e.getMessage()));
        }
    }

    private AuthorizationResponse parseResponse(HttpServletRequest request) {
        try {
            return AuthorizationResponse.parse(URI.create(request.getRequestURL().toString()), Maps.transformValues((Map)request.getParameterMap(), ImmutableList::copyOf));
        }
        catch (ParseException e) {
            logger.warn("Parsing authorization response failed", (Throwable)e);
            return null;
        }
    }

    private FlowRequestData tryFetchFlowRequestData(HttpServletRequest request, AuthorizationResponse authorizationResponse) {
        String state = this.getStateFromResponse(authorizationResponse);
        if (SystemProperty.DISABLE_CLIENT_STATE_VALIDATION.getValue().booleanValue() && state == null) {
            logger.warn("Can't complete OAuth 2.0 authorization code flow - no state returned by authorization server");
            return null;
        }
        try {
            FlowRequestData flowData = this.servletFlowRequestService.fetchFlowRequestDataByState(request.getSession(false), state);
            if (!this.verifyFlowState(state, flowData.getState())) {
                return null;
            }
            return flowData;
        }
        catch (Exception e) {
            logger.warn("Can't complete OAuth 2.0 authorization code flow - unknown state returned by authorization server");
            return null;
        }
    }

    private void startFlow(HttpServletRequest request, HttpServletResponse response, String flowId) throws IOException {
        FlowRequestData flowRequestData;
        try {
            logger.debug("Starting new OAuth 2.0 authorization code flow with a flow id [{}]", (Object)flowId);
            flowRequestData = this.servletFlowRequestService.fetchFlowRequestDataById(request.getSession(false), flowId);
        }
        catch (Exception e) {
            logger.warn("Can't initiate OAuth 2.0 authorization code flow - no data available for the given flow ID");
            this.sendRedirectToBaseUrl(response);
            return;
        }
        try {
            ClientConfiguration config = flowRequestData.getClientConfiguration();
            AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(new ResponseType(ResponseType.Value.CODE), new ClientID(config.getClientId())).scope(Scope.parse(config.getScopes())).state(new State(flowRequestData.getState())).redirectionURI(this.authorizationCodeFlowUrlsProvider.getRedirectUri(config)).endpointURI(URI.create(config.getAuthorizationEndpoint()));
            Prompt prompt = new Prompt(Prompt.Type.SELECT_ACCOUNT);
            Scope scopes = Scope.parse(config.getScopes());
            if (config.getProviderType() == ProviderType.MICROSOFT) {
                scopes.add("offline_access");
            } else if (config.getProviderType() == ProviderType.GOOGLE) {
                builder.customParameter("access_type", "offline");
                prompt.add(Prompt.Type.CONSENT);
            }
            AuthorizationRequest authnRequest = builder.scope(scopes).prompt(prompt).build();
            logger.debug("Sending a redirect to the URI to ask for a consent for an application - [{}]", (Object)authnRequest.toURI().toString());
            response.sendRedirect(authnRequest.toURI().toString());
        }
        catch (RuntimeException e) {
            this.servletFlowRequestService.updateFlowRequest(request.getSession(false), flowRequestData, (FlowRequestError)new FlowRequestErrorImpl(e.getMessage()));
            response.sendRedirect(flowRequestData.getClientRedirectUrl());
        }
    }

    private void sendRedirectToBaseUrl(HttpServletResponse response) throws IOException {
        response.sendRedirect(this.authorizationCodeFlowUrlsProvider.getProductBaseUrl().toString());
    }

    private String getStateFromResponse(AuthorizationResponse authorizationResponse) {
        return Optional.ofNullable(authorizationResponse.getState()).map(Identifier::getValue).orElse(null);
    }

    private boolean verifyFlowState(String expectedState, String flowState) {
        if (SystemProperty.DISABLE_CLIENT_STATE_VALIDATION.getValue().booleanValue()) {
            return true;
        }
        if (expectedState == null || flowState == null) {
            return false;
        }
        return expectedState.equals(flowState);
    }
}

