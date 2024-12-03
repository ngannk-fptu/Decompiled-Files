/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.access.AccessToken
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.service.grant;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.rest.exception.InvalidGrantException;
import com.atlassian.oauth2.provider.rest.model.RestToken;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;
import com.atlassian.oauth2.provider.rest.service.TokenRestService;
import com.atlassian.oauth2.provider.rest.service.grant.GrantProcessor;
import com.atlassian.oauth2.provider.rest.validation.grant.GrantValidator;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshTokenGrantProcessor
implements GrantProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenGrantProcessor.class);
    private static final String REFRESH_CODE_GRANT_LOCK = "com.atlassian.oauth2.provider.refresh.code.grant.lock";
    private final TokenService tokenService;
    private final I18nResolver i18nResolver;
    private final GrantValidator grantValidator;
    private final ClusterLockService clusterLockService;
    private final JwtService jwtService;

    public RefreshTokenGrantProcessor(TokenService tokenService, I18nResolver i18nResolver, GrantValidator grantValidator, ClusterLockService clusterLockService, JwtService jwtService) {
        this.tokenService = tokenService;
        this.i18nResolver = i18nResolver;
        this.grantValidator = grantValidator;
        this.clusterLockService = clusterLockService;
        this.jwtService = jwtService;
    }

    @Override
    public RestToken execute(TokenRequestFormParams formParams) throws InvalidGrantException, InterruptedException {
        this.grantValidator.validate(formParams);
        String refreshTokenId = this.jwtService.extractTokenId(formParams.getRefreshToken());
        Optional oldRefresh = this.tokenService.findByRefreshTokenId(refreshTokenId);
        ClusterLock lock = this.clusterLockService.getLockForName(REFRESH_CODE_GRANT_LOCK);
        logger.debug("Attempting to obtain lock for [{}]", (Object)REFRESH_CODE_GRANT_LOCK);
        if (lock.tryLock(SystemProperty.GLOBAL_CLUSTER_LOCK_TIMEOUT_SECONDS.getValue().intValue(), TimeUnit.SECONDS)) {
            logger.debug("Obtained lock for [{}]", (Object)REFRESH_CODE_GRANT_LOCK);
            try {
                if (oldRefresh.isPresent()) {
                    RefreshToken oldRefreshToken = (RefreshToken)oldRefresh.get();
                    logger.debug("Removing tokens associated with refresh token id [{}]", (Object)refreshTokenId);
                    this.tokenService.removeAccessTokenAssociatedWith(refreshTokenId);
                    this.tokenService.removeRefreshToken(refreshTokenId);
                    AccessToken newAccessToken = this.tokenService.createAccessToken(formParams.getClientId(), oldRefreshToken.getUserKey(), oldRefreshToken.getAuthorizationCode(), oldRefreshToken.getAuthorizationDate().longValue(), oldRefreshToken.getScope());
                    RefreshToken newRefreshToken = this.tokenService.createRefreshToken(formParams.getClientId(), oldRefreshToken.getUserKey(), oldRefreshToken.getAuthorizationDate().longValue(), newAccessToken.getId(), oldRefreshToken.getScope(), oldRefreshToken.getAuthorizationCode(), oldRefreshToken.getRefreshCount() + 1);
                    RestToken restToken = TokenRestService.tokenEntityToRestTokenWithRefresh(this.jwtService.createToken(newAccessToken.getId()), this.jwtService.createToken(newRefreshToken.getId()), oldRefreshToken.getScope().toString());
                    return restToken;
                }
                throw new InvalidGrantException(this.i18nResolver.getText("oauth2.rest.error.invalid.refresh.token"));
            }
            finally {
                lock.unlock();
            }
        }
        throw new IllegalMonitorStateException(this.i18nResolver.getText("oauth2.rest.error.multiple.create.attempts.error"));
    }
}

