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
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.servlet.http.HttpSession
 */
package com.atlassian.confluence.mail.archive.oauth;

import com.atlassian.confluence.mail.archive.oauth.OAuthManager;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequest;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestService;
import com.atlassian.oauth2.client.api.lib.flow.FlowResult;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigurationEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpSession;

public class DefaultOAuthManager
implements OAuthManager {
    public static final String FLOW_ID = "flow-id";
    private final ClientConfigStorageService clientConfigStorageService;
    private final ClientTokenStorageService clientTokenStorageService;
    private final FlowRequestService flowRequestService;

    public DefaultOAuthManager(@ComponentImport ApplicationProperties applicationProperties, ClientConfigStorageService clientConfigStorageService, ClientTokenStorageService clientTokenStorageService, FlowRequestService flowRequestService) {
        this.clientConfigStorageService = Objects.requireNonNull(clientConfigStorageService);
        this.clientTokenStorageService = Objects.requireNonNull(clientTokenStorageService);
        this.flowRequestService = Objects.requireNonNull(flowRequestService);
    }

    @Override
    public List<OAuthManager.OAuthProvider> getConfiguredOAuthProvider() {
        return this.clientConfigStorageService.list().stream().map(entity -> new OAuthManager.OAuthProvider(entity.getId(), entity.getName(), entity.getProviderType().getKey())).collect(Collectors.toList());
    }

    @Override
    public OAuthManager.OAuthResult initialiseOAuthFlow(HttpSession session, String oAuthProviderId, Function<String, String> buildRedirect) {
        ClientConfigurationEntity configurationEntity = (ClientConfigurationEntity)this.clientConfigStorageService.getById(oAuthProviderId).orElseThrow(() -> new IllegalArgumentException("No Authentication Method found for id: " + oAuthProviderId));
        FlowRequest flowRequest = this.flowRequestService.createFlowRequest(session, (ClientConfiguration)configurationEntity, buildRedirect);
        session.setAttribute(FLOW_ID, (Object)flowRequest.getId());
        return new OAuthManager.OAuthResult(flowRequest.getId(), flowRequest.getInitFlowUrl());
    }

    @Override
    public String completeOAuthFlow(HttpSession session, String oAuthProviderId) throws Exception {
        Object flowId = session.getAttribute(FLOW_ID);
        if (flowId == null) {
            throw new Exception("No Flow Identifier found for OAuth Provider Id: " + oAuthProviderId);
        }
        FlowResult flowResult = this.flowRequestService.getFlowResult(session, flowId.toString());
        if (flowResult.indicatesSuccess()) {
            ClientToken clientToken = flowResult.toSuccessResult();
            ClientTokenEntity savedToken = this.clientTokenStorageService.save(ClientTokenEntity.builder((ClientToken)clientToken).lastStatusUpdated(Instant.now()).configId(oAuthProviderId).build());
            return savedToken.getId();
        }
        throw new Exception(flowResult.toErrorResult().getMessage());
    }
}

