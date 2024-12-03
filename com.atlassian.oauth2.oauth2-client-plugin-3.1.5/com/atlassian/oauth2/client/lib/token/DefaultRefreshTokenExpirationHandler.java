/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.client.lib.token;

import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.lib.token.RefreshTokenExpirationHandler;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRefreshTokenExpirationHandler
implements RefreshTokenExpirationHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRefreshTokenExpirationHandler.class);
    private final Duration defaultRefreshTokenExpirationDuration;

    public DefaultRefreshTokenExpirationHandler(Duration defaultRefreshTokenExpirationDuration) {
        this.defaultRefreshTokenExpirationDuration = defaultRefreshTokenExpirationDuration;
    }

    @Override
    public Instant getExpirationTimeForToken(ClientConfiguration clientConfiguration, Instant now, RefreshToken refreshToken) {
        Instant defaultExpirationTime = now.plus(this.defaultRefreshTokenExpirationDuration);
        switch (clientConfiguration.getProviderType()) {
            case GOOGLE: {
                return defaultExpirationTime;
            }
            case MICROSOFT: {
                return defaultExpirationTime;
            }
        }
        return this.parseRefreshTokenForExpiration(refreshToken, defaultExpirationTime);
    }

    private Instant parseRefreshTokenForExpiration(RefreshToken refreshToken, Instant defaultExpirationTime) {
        try {
            JOSEObject jwt = JOSEObject.parse(refreshToken.getValue());
            return Optional.ofNullable(jwt.getPayload()).map(Payload::toJSONObject).map(jsonObject -> (Long)jsonObject.get("exp")).map(Instant::ofEpochSecond).orElse(defaultExpirationTime);
        }
        catch (Exception e) {
            logger.debug("Error when trying to get expiration time of the refresh token, using default value - {} days. The reason is: {}", (Object)Duration.ofMillis(defaultExpirationTime.toEpochMilli()).toDays(), (Object)e.getMessage());
            return defaultExpirationTime;
        }
    }
}

