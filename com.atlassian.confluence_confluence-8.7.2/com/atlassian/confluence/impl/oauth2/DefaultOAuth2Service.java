/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequest
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestService
 *  com.atlassian.oauth2.client.api.lib.flow.FlowResult
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.http.HttpSession
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.oauth2;

import com.atlassian.confluence.oauth2.OAuth2Exception;
import com.atlassian.confluence.oauth2.OAuth2Service;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequest;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestService;
import com.atlassian.oauth2.client.api.lib.flow.FlowResult;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import com.google.common.annotations.VisibleForTesting;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultOAuth2Service
implements OAuth2Service {
    private static final Logger log = LoggerFactory.getLogger(DefaultOAuth2Service.class);
    @VisibleForTesting
    static final String FLOW_ID = "flow-id";
    private final ClientConfigStorageService clientConfigStorageService;
    private final ClientTokenStorageService clientTokenStorageService;
    private final FlowRequestService flowRequestService;

    public DefaultOAuth2Service(ClientConfigStorageService clientConfigStorageService, ClientTokenStorageService clientTokenStorageService, FlowRequestService flowRequestService) {
        this.clientConfigStorageService = Objects.requireNonNull(clientConfigStorageService);
        this.clientTokenStorageService = Objects.requireNonNull(clientTokenStorageService);
        this.flowRequestService = Objects.requireNonNull(flowRequestService);
    }

    @Override
    public List<OAuth2Service.OAuth2Provider> getConfiguredOAuth2Providers() {
        return this.clientConfigStorageService.list().stream().map(entity -> new OAuth2Service.OAuth2Provider(entity.getId(), entity.getName(), entity.getProviderType().getKey())).collect(Collectors.toList());
    }

    @Override
    public OAuth2Service.OAuth2Result initialiseOAuth2Flow(HttpSession session, String oAuth2ProviderId, UnaryOperator<String> buildRedirect) throws IllegalArgumentException {
        ClientConfigurationEntity configurationEntity = (ClientConfigurationEntity)this.clientConfigStorageService.getById(oAuth2ProviderId).orElseThrow(() -> new IllegalArgumentException("No Authorization Method found for id: " + oAuth2ProviderId));
        FlowRequest flowRequest = this.flowRequestService.createFlowRequest(session, (ClientConfiguration)configurationEntity, buildRedirect);
        session.setAttribute(FLOW_ID, (Object)flowRequest.getId());
        return new OAuth2Service.OAuth2Result(flowRequest.getId(), flowRequest.getInitFlowUrl());
    }

    @Override
    public String completeOAuth2Flow(HttpSession session, String oAuthProviderId) throws OAuth2Exception {
        Object flowId = session.getAttribute(FLOW_ID);
        if (flowId == null) {
            throw new OAuth2Exception("No Flow Identifier found for OAuth Provider Id: " + oAuthProviderId);
        }
        FlowResult flowResult = this.flowRequestService.getFlowResult(session, flowId.toString());
        if (flowResult.indicatesSuccess()) {
            ClientToken clientToken = flowResult.toSuccessResult();
            try {
                ClientTokenEntity savedToken = this.clientTokenStorageService.save(ClientTokenEntity.builder((ClientToken)clientToken).lastStatusUpdated(Instant.now()).configId(oAuthProviderId).build());
                return savedToken.getId();
            }
            catch (TokenNotFoundException tokenNotFoundException) {
                throw new OAuth2Exception(tokenNotFoundException);
            }
        }
        throw new OAuth2Exception(flowResult.toErrorResult().getMessage());
    }

    @Override
    public ClientTokenEntity getToken(String tokenId) throws OAuth2Exception {
        try {
            return this.clientTokenStorageService.getByIdOrFail(tokenId);
        }
        catch (TokenNotFoundException e) {
            throw new OAuth2Exception(e);
        }
    }
}

