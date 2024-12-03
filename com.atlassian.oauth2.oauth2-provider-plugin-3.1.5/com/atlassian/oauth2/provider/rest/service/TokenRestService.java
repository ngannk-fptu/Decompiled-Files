/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.service;

import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.rest.exception.InvalidGrantException;
import com.atlassian.oauth2.provider.rest.model.RestToken;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;
import com.atlassian.oauth2.provider.rest.service.grant.GrantProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenRestService {
    private static final Logger logger = LoggerFactory.getLogger(TokenRestService.class);
    private static final String BEARER_TOKEN = "bearer";
    private final GrantProcessorFactory grantProcessorFactory;

    public TokenRestService(GrantProcessorFactory grantProcessorFactory) {
        this.grantProcessorFactory = grantProcessorFactory;
    }

    public RestToken create(TokenRequestFormParams formParams) throws InvalidGrantException, InterruptedException {
        return this.grantProcessorFactory.createGrantProcessor(formParams.getGrantType()).execute(formParams);
    }

    public static RestToken tokenEntityToRestTokenWithRefresh(String accessToken, String refreshToken, String scope) {
        logger.debug("Mapping token entity to rest token");
        return RestToken.builder().accessToken(accessToken).tokenType(BEARER_TOKEN).refreshToken(refreshToken).expiresIn(SystemProperty.MAX_ACCESS_TOKEN_LIFETIME.getValue().getSeconds()).scope(scope).build();
    }
}

