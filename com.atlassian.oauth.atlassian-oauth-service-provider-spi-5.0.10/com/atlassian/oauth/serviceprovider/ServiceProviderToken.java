/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Token
 *  com.atlassian.oauth.Token$TokenBuilder
 *  com.atlassian.oauth.Token$Type
 *  com.atlassian.oauth.util.Check
 *  javax.annotation.Nullable
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.oauth.serviceprovider;

import com.atlassian.oauth.Token;
import com.atlassian.oauth.serviceprovider.Clock;
import com.atlassian.oauth.serviceprovider.SystemPropertyUtils;
import com.atlassian.oauth.util.Check;
import java.net.URI;
import java.security.Principal;
import java.util.Objects;
import javax.annotation.Nullable;
import net.jcip.annotations.Immutable;

@Immutable
public final class ServiceProviderToken
extends Token {
    public static final long DEFAULT_REQUEST_TOKEN_TTL = SystemPropertyUtils.parsePositiveLongFromSystemProperty("atlassian.oauth.default.request.token.ttl", 600000L);
    public static final long DEFAULT_ACCESS_TOKEN_TTL = SystemPropertyUtils.parsePositiveLongFromSystemProperty("atlassian.oauth.default.access.token.ttl", 157680000000L);
    public static final long DEFAULT_SESSION_TTL = SystemPropertyUtils.parsePositiveLongFromSystemProperty("atlassian.oauth.default.session.ttl", DEFAULT_ACCESS_TOKEN_TTL + 2592000000L);
    private final Authorization authorization;
    private final Principal user;
    private final String verifier;
    private final long creationTime;
    private final long timeToLive;
    private final URI callback;
    private final Version version;
    private final Session session;

    private ServiceProviderToken(ServiceProviderTokenBuilder builder) {
        super((Token.TokenBuilder)builder);
        if (this.isAccessToken()) {
            Objects.requireNonNull(builder.user, "user must be set for access tokens");
        } else {
            Objects.requireNonNull(builder.version, "version must be set for request tokens");
            if (Version.V_1_0_A.equals((Object)builder.version) && builder.user != null && builder.authorization == Authorization.AUTHORIZED) {
                Check.notBlank((String)builder.verifier, (Object)"verifier MUST NOT be blank if the request token has been authorized");
            }
        }
        if (builder.callback != null && !ServiceProviderToken.isValidCallback(builder.callback)) {
            throw new IllegalArgumentException("callback must be null or a valid, absolute URI using either the http or https scheme");
        }
        this.authorization = builder.authorization;
        this.user = builder.user;
        this.verifier = builder.verifier;
        this.creationTime = builder.creationTime;
        this.timeToLive = builder.timeToLive;
        this.callback = builder.callback;
        this.version = builder.version;
        this.session = builder.session;
    }

    public static ServiceProviderTokenBuilder newRequestToken(String token) {
        return new ServiceProviderTokenBuilder(Token.Type.REQUEST, Objects.requireNonNull(token, "token"));
    }

    public static ServiceProviderTokenBuilder newAccessToken(String token) {
        return new ServiceProviderTokenBuilder(Token.Type.ACCESS, Objects.requireNonNull(token, "token"));
    }

    public ServiceProviderToken authorize(Principal user, String verifier) {
        Objects.requireNonNull(user, "user");
        if (Version.V_1_0_A.equals((Object)this.version)) {
            Check.notBlank((String)verifier, (Object)"verifier");
        }
        if (!this.isRequestToken()) {
            throw new IllegalStateException("token is not a request token");
        }
        if (this.hasBeenAuthorized()) {
            throw new IllegalStateException("token has already been authorized");
        }
        if (this.hasBeenDenied()) {
            throw new IllegalStateException("token has already been denied");
        }
        return ((ServiceProviderTokenBuilder)((ServiceProviderTokenBuilder)((ServiceProviderTokenBuilder)ServiceProviderToken.newRequestToken(this.getToken()).tokenSecret(this.getTokenSecret())).consumer(this.getConsumer())).authorizedBy(user).verifier(verifier).creationTime(this.creationTime).timeToLive(this.timeToLive).properties(this.getProperties())).callback(this.callback).version(this.version).build();
    }

    public boolean hasBeenAuthorized() {
        return this.getAuthorization() == Authorization.AUTHORIZED;
    }

    public ServiceProviderToken deny(Principal user) {
        Objects.requireNonNull(user, "user");
        if (!this.isRequestToken()) {
            throw new IllegalStateException("token is not a request token");
        }
        if (this.hasBeenAuthorized()) {
            throw new IllegalStateException("token has already been authorized");
        }
        if (this.hasBeenDenied()) {
            throw new IllegalStateException("token has already been denied");
        }
        return ((ServiceProviderTokenBuilder)((ServiceProviderTokenBuilder)((ServiceProviderTokenBuilder)ServiceProviderToken.newRequestToken(this.getToken()).tokenSecret(this.getTokenSecret())).consumer(this.getConsumer())).deniedBy(user).creationTime(this.creationTime).timeToLive(this.timeToLive).properties(this.getProperties())).callback(this.callback).version(this.version).build();
    }

    public boolean hasBeenDenied() {
        return this.getAuthorization() == Authorization.DENIED;
    }

    public Authorization getAuthorization() {
        return this.authorization;
    }

    @Nullable
    public Principal getUser() {
        return this.user;
    }

    @Nullable
    public String getVerifier() {
        return this.verifier;
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getTimeToLive() {
        return this.timeToLive;
    }

    public boolean hasExpired(Clock clock) {
        return clock.timeInMilliseconds() - this.creationTime > this.timeToLive;
    }

    @Nullable
    public URI getCallback() {
        return this.callback;
    }

    public static boolean isValidCallback(URI callback) {
        return callback.isAbsolute() && ("https".equals(callback.getScheme()) || "http".equals(callback.getScheme()));
    }

    public Version getVersion() {
        return this.version;
    }

    public Session getSession() {
        return this.session;
    }

    public boolean hasSession() {
        return this.session != null;
    }

    public static final class ServiceProviderTokenBuilder
    extends Token.TokenBuilder<ServiceProviderToken, ServiceProviderTokenBuilder> {
        private Authorization authorization;
        private Principal user;
        private String verifier;
        private long creationTime;
        private long timeToLive;
        private URI callback;
        private Version version;
        private Session session;

        private ServiceProviderTokenBuilder(Token.Type type, String token) {
            super(type, token);
            if (type == Token.Type.ACCESS) {
                this.timeToLive = DEFAULT_ACCESS_TOKEN_TTL;
                this.authorization = Authorization.AUTHORIZED;
            } else {
                this.timeToLive = DEFAULT_REQUEST_TOKEN_TTL;
                this.authorization = Authorization.NONE;
            }
        }

        public ServiceProviderTokenBuilder authorizedBy(Principal user) {
            this.user = user;
            this.authorization = Authorization.AUTHORIZED;
            return this;
        }

        public ServiceProviderTokenBuilder deniedBy(Principal user) {
            this.user = user;
            this.authorization = Authorization.DENIED;
            return this;
        }

        public ServiceProviderTokenBuilder verifier(String verifier) {
            this.verifier = verifier;
            return this;
        }

        public ServiceProviderTokenBuilder creationTime(long creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public ServiceProviderTokenBuilder timeToLive(long timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        public ServiceProviderTokenBuilder callback(@Nullable URI callback) {
            this.callback = callback;
            return this;
        }

        public ServiceProviderTokenBuilder version(Version version) {
            this.version = version;
            return this;
        }

        public ServiceProviderTokenBuilder session(Session session) {
            this.session = session;
            return this;
        }

        public ServiceProviderToken build() {
            if (this.creationTime == 0L) {
                this.creationTime = System.currentTimeMillis();
            }
            return new ServiceProviderToken(this);
        }
    }

    public static final class Session {
        private final String handle;
        private final long creationTime;
        private final long lastRenewalTime;
        private final long timeToLive;

        Session(Builder builder) {
            this.handle = builder.handle;
            this.creationTime = builder.creationTime;
            this.lastRenewalTime = builder.lastRenewalTime;
            this.timeToLive = builder.timeToLive;
        }

        public String getHandle() {
            return this.handle;
        }

        public long getCreationTime() {
            return this.creationTime;
        }

        public long getLastRenewalTime() {
            return this.lastRenewalTime;
        }

        public long getTimeToLive() {
            return this.timeToLive;
        }

        public static Builder newSession(String handle) {
            return new Builder(handle);
        }

        public boolean hasExpired(Clock clock) {
            return clock.timeInMilliseconds() - this.lastRenewalTime > this.timeToLive;
        }

        public static final class Builder {
            private final String handle;
            private long creationTime;
            private long lastRenewalTime;
            private long timeToLive;

            Builder(String handle) {
                this.lastRenewalTime = this.creationTime = System.currentTimeMillis();
                this.timeToLive = DEFAULT_SESSION_TTL;
                this.handle = Check.notBlank((String)handle, (Object)"handle");
            }

            public Builder creationTime(long creationTime) {
                this.creationTime = creationTime;
                return this;
            }

            public Builder lastRenewalTime(long lastRenewalTime) {
                this.lastRenewalTime = lastRenewalTime;
                return this;
            }

            public Builder timeToLive(long timeToLive) {
                this.timeToLive = timeToLive;
                return this;
            }

            public Session build() {
                return new Session(this);
            }
        }
    }

    public static enum Version {
        V_1_0,
        V_1_0_A;

    }

    public static enum Authorization {
        NONE,
        AUTHORIZED,
        DENIED;

    }
}

