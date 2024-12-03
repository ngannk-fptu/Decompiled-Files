/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.bridge.Requests
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Authorization
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$ServiceProviderTokenBuilder
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Session
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Session$Builder
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Version
 *  com.atlassian.oauth.serviceprovider.TokenPropertiesFactory
 *  javax.annotation.Nullable
 *  net.oauth.OAuthMessage
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.bridge.Requests;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.TokenPropertiesFactory;
import com.atlassian.oauth.serviceprovider.internal.Randomizer;
import com.atlassian.oauth.serviceprovider.internal.TokenFactory;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nullable;
import net.oauth.OAuthMessage;
import org.springframework.beans.factory.annotation.Qualifier;

public class TokenFactoryImpl
implements TokenFactory {
    private final TokenPropertiesFactory propertiesFactory;
    private final Randomizer randomizer;

    public TokenFactoryImpl(@Qualifier(value="aggregatePropertiesFactory") TokenPropertiesFactory propertiesFactory, Randomizer randomizer) {
        this.propertiesFactory = Objects.requireNonNull(propertiesFactory, "propertiesFactory");
        this.randomizer = Objects.requireNonNull(randomizer, "randomizer");
    }

    @Override
    public ServiceProviderToken generateRequestToken(Consumer consumer, @Nullable URI callback, OAuthMessage message, ServiceProviderToken.Version version) {
        Objects.requireNonNull(consumer, "consumer");
        String token = this.randomizer.randomAlphanumericString(32);
        String secret = this.randomizer.randomAlphanumericString(32);
        return ((ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)ServiceProviderToken.newRequestToken((String)token).tokenSecret(secret)).consumer(consumer)).callback(callback).version(version).properties(this.propertiesFactory.newRequestTokenProperties(Requests.fromOAuthMessage((OAuthMessage)message)))).build();
    }

    @Override
    public ServiceProviderToken generateAccessToken(ServiceProviderToken token) {
        Objects.requireNonNull(token, "token");
        if (token.isRequestToken() && token.getAuthorization() != ServiceProviderToken.Authorization.AUTHORIZED) {
            throw new IllegalArgumentException("token is not an authorized request token");
        }
        String t = this.randomizer.randomAlphanumericString(32);
        return ((ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)ServiceProviderToken.newAccessToken((String)t).tokenSecret(token.getTokenSecret())).consumer(token.getConsumer())).authorizedBy(token.getUser()).properties(this.propertiesFactory.newAccessTokenProperties(token))).session(this.newSession(token)).build();
    }

    private ServiceProviderToken.Session newSession(ServiceProviderToken token) {
        ServiceProviderToken.Session.Builder builder = ServiceProviderToken.Session.newSession((String)this.randomizer.randomAlphanumericString(32));
        if (token.getSession() != null) {
            builder.creationTime(token.getSession().getCreationTime());
        }
        return builder.build();
    }
}

