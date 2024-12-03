/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.ClientTokenMetadata$ClientTokenStatus
 *  com.atlassian.oauth2.client.api.lib.token.TokenService
 *  com.atlassian.oauth2.client.api.lib.token.TokenServiceException
 *  com.atlassian.oauth2.client.api.storage.TokenHandler
 *  com.atlassian.oauth2.client.api.storage.TokenHandler$ClientTokenCallback
 *  com.atlassian.oauth2.client.api.storage.TokenHandler$InvalidTokenException
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.event.ClientTokenRecoverableEvent
 *  com.atlassian.oauth2.client.api.storage.event.ClientTokenUnrecoverableEvent
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity$Builder
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  com.atlassian.oauth2.client.api.storage.token.exception.AccessTokenExpiredException
 *  com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException
 *  com.atlassian.oauth2.client.api.storage.token.exception.RecoverableTokenException
 *  com.atlassian.oauth2.client.api.storage.token.exception.RefreshTokenExpiredException
 *  com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException
 *  com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException
 *  com.google.common.base.Throwables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.client.storage;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.ClientTokenMetadata;
import com.atlassian.oauth2.client.api.lib.token.TokenService;
import com.atlassian.oauth2.client.api.lib.token.TokenServiceException;
import com.atlassian.oauth2.client.api.storage.TokenHandler;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenRecoverableEvent;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenUnrecoverableEvent;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.oauth2.client.api.storage.token.exception.AccessTokenExpiredException;
import com.atlassian.oauth2.client.api.storage.token.exception.ConfigurationNotFoundException;
import com.atlassian.oauth2.client.api.storage.token.exception.RecoverableTokenException;
import com.atlassian.oauth2.client.api.storage.token.exception.RefreshTokenExpiredException;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException;
import com.atlassian.oauth2.client.properties.SystemProperty;
import com.atlassian.oauth2.common.concurrent.KeyedLocks;
import com.google.common.base.Throwables;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTokenHandler
implements TokenHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenHandler.class);
    @VisibleForTesting
    static final int MAX_EXECUTE_ATTEMPTS = 2;
    private static final Duration LARGE_MARGIN = Duration.ofDays(3650L);
    private final ClientTokenStorageService clientTokenStorageService;
    private final ClientConfigStorageService clientConfigStorageService;
    private final TokenService tokenService;
    private final Clock clock;
    private final KeyedLocks<String> tokenLocks = new KeyedLocks(SystemProperty.DEFAULT_MONITOR_STRIPE_COUNT.getValue());
    private final Duration minFailingPeriodForUnrecoverable;
    private final EventPublisher eventPublisher;

    public DefaultTokenHandler(ClientTokenStorageService clientTokenStorageService, ClientConfigStorageService clientConfigStorageService, TokenService tokenService, Clock clock, Duration minFailingPeriodForUnrecoverable, EventPublisher eventPublisher) {
        this.clientTokenStorageService = clientTokenStorageService;
        this.clientConfigStorageService = clientConfigStorageService;
        this.tokenService = tokenService;
        this.clock = clock;
        this.minFailingPeriodForUnrecoverable = minFailingPeriodForUnrecoverable;
        this.eventPublisher = eventPublisher;
    }

    public <T> T execute(String clientTokenId, TokenHandler.ClientTokenCallback<T> callback) throws UnrecoverableTokenException, RecoverableTokenException {
        return this.execute(clientTokenId, callback, LARGE_MARGIN);
    }

    public <T> T execute(String clientTokenId, TokenHandler.ClientTokenCallback<T> callback, Duration margin) throws UnrecoverableTokenException, RecoverableTokenException {
        ClientTokenEntity clientToken = null;
        TokenHandler.InvalidTokenException caught = null;
        for (int attempt = 1; attempt <= 2; ++attempt) {
            clientToken = this.getRefreshedToken(clientTokenId, margin, false);
            try {
                Object result = callback.apply((ClientToken)clientToken);
                this.updateToken(clientToken, token -> token.status(ClientTokenMetadata.ClientTokenStatus.VALID));
                return (T)result;
            }
            catch (TokenHandler.InvalidTokenException e) {
                caught = e;
                logger.debug("Token with ID {} reported as invalid during attempt {} of {}", new Object[]{clientTokenId, attempt, 2});
                logger.trace("Exception caught: ", (Throwable)e);
                continue;
            }
        }
        return this.handleFailure(clientToken, caught.getMessage(), (Exception)((Object)caught));
    }

    public ClientTokenEntity getRefreshedToken(String clientTokenId) throws UnrecoverableTokenException, RecoverableTokenException {
        return this.getRefreshedToken(clientTokenId, LARGE_MARGIN);
    }

    public ClientTokenEntity getRefreshedToken(String clientTokenId, Duration margin) throws UnrecoverableTokenException, RecoverableTokenException {
        return this.getRefreshedToken(clientTokenId, margin, true);
    }

    private ClientTokenEntity getRefreshedToken(String clientTokenId, Duration margin, boolean allowRecovery) throws UnrecoverableTokenException, RecoverableTokenException {
        try {
            return this.tokenLocks.executeWithLock(clientTokenId, () -> this.refreshTokenIfNeeded(this.clientTokenStorageService.getByIdOrFail(clientTokenId), margin, allowRecovery));
        }
        catch (Exception e) {
            Throwables.propagateIfPossible((Throwable)e, UnrecoverableTokenException.class, RecoverableTokenException.class);
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    private ClientTokenEntity refreshTokenIfNeeded(ClientTokenEntity clientTokenEntity, Duration margin, boolean allowRecovery) throws UnrecoverableTokenException, RecoverableTokenException {
        if (clientTokenEntity.getStatus() == ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE) {
            throw new UnrecoverableTokenException("Token already marked as invalid");
        }
        if (clientTokenEntity.getRefreshToken() == null) {
            Instant now = this.clock.instant().minus(SystemProperty.MAX_CLOCK_SKEW.getValue());
            if (clientTokenEntity.getAccessTokenExpiration().isBefore(now)) {
                this.updateToken(clientTokenEntity, token -> token.status(ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE));
                throw new AccessTokenExpiredException("Cannot refresh the access token as the refresh token is not present");
            }
            return clientTokenEntity;
        }
        return this.tokenService.isRefreshNeeded((ClientToken)clientTokenEntity, margin) ? this.refreshToken(clientTokenEntity, allowRecovery) : clientTokenEntity;
    }

    private ClientTokenEntity refreshToken(ClientTokenEntity clientTokenEntity, boolean allowRecovery) throws UnrecoverableTokenException, RecoverableTokenException {
        Optional clientConfiguration = this.clientConfigStorageService.getById(clientTokenEntity.getConfigId());
        if (!clientConfiguration.isPresent()) {
            this.updateToken(clientTokenEntity, token -> token.status(ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE));
            throw new ConfigurationNotFoundException("Cannot refresh token as client configuration does not exist");
        }
        try {
            ClientToken refreshedToken = this.tokenService.forceRefresh((ClientConfiguration)clientConfiguration.get(), (ClientToken)clientTokenEntity);
            boolean shouldRecover = allowRecovery && clientTokenEntity.getStatus() == ClientTokenMetadata.ClientTokenStatus.RECOVERABLE;
            return this.updateToken(clientTokenEntity, token -> token.updateFrom(refreshedToken).lastRefreshed(this.clock.instant()).status(shouldRecover ? ClientTokenMetadata.ClientTokenStatus.UNKNOWN : clientTokenEntity.getStatus()).incrementRefreshCount());
        }
        catch (TokenServiceException e) {
            Instant now = this.clock.instant().minus(SystemProperty.MAX_CLOCK_SKEW.getValue());
            if (clientTokenEntity.getRefreshTokenExpiration().isBefore(now)) {
                this.updateToken(clientTokenEntity, token -> token.status(ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE));
                throw new RefreshTokenExpiredException("Cannot refresh the access token as the refresh token has expired");
            }
            return (ClientTokenEntity)this.handleFailure(clientTokenEntity, "An error has occurred while refreshing OAuth token", (Exception)((Object)e));
        }
    }

    private <T> T handleFailure(ClientTokenEntity clientTokenEntity, String message, Exception exception) throws RecoverableTokenException, UnrecoverableTokenException {
        Duration failingPeriod;
        if (clientTokenEntity.getStatus() == ClientTokenMetadata.ClientTokenStatus.RECOVERABLE && (failingPeriod = Duration.between(clientTokenEntity.getLastStatusUpdated(), this.clock.instant())).compareTo(this.minFailingPeriodForUnrecoverable) >= 0) {
            this.updateToken(clientTokenEntity, toUpdate -> toUpdate.status(ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE));
            throw new UnrecoverableTokenException("Token already marked as invalid");
        }
        ClientTokenEntity updated = this.updateToken(clientTokenEntity, token -> token.status(ClientTokenMetadata.ClientTokenStatus.RECOVERABLE));
        throw new RecoverableTokenException(message, (Throwable)exception, updated.getLastStatusUpdated());
    }

    private ClientTokenEntity updateToken(ClientTokenEntity tokenEntity, Consumer<ClientTokenEntity.Builder> update) throws TokenNotFoundException {
        ClientTokenEntity.Builder builder = ClientTokenEntity.builder((ClientTokenEntity)tokenEntity);
        update.accept(builder);
        if (!tokenEntity.getStatus().equals((Object)builder.getStatus()) || builder.getLastStatusUpdated() == null) {
            builder.lastStatusUpdated(this.clock.instant());
        }
        ClientTokenEntity updatedToken = builder.build();
        if (!tokenEntity.getStatus().equals((Object)updatedToken.getStatus())) {
            if (updatedToken.getStatus().equals((Object)ClientTokenMetadata.ClientTokenStatus.RECOVERABLE)) {
                this.eventPublisher.publish((Object)new ClientTokenRecoverableEvent(updatedToken.getId()));
            } else if (updatedToken.getStatus().equals((Object)ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE)) {
                this.eventPublisher.publish((Object)new ClientTokenUnrecoverableEvent(updatedToken.getId()));
            }
        }
        return Objects.equals(tokenEntity, updatedToken) ? updatedToken : this.clientTokenStorageService.save(updatedToken);
    }

    @VisibleForTesting
    int getTokensUnderRefreshCount() {
        return this.tokenLocks.size();
    }
}

