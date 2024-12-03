/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth.Token
 *  com.atlassian.oauth.event.AccessTokenAddedEvent
 *  com.atlassian.oauth.event.AccessTokenRemovedEvent
 *  com.atlassian.oauth.event.RequestTokenAddedEvent
 *  com.atlassian.oauth.event.RequestTokenRemovedEvent
 *  com.atlassian.oauth.serviceprovider.Clock
 *  com.atlassian.oauth.serviceprovider.InvalidTokenException
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Authorization
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$ServiceProviderTokenBuilder
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Session
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Version
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.oauth.serviceprovider.StoreException
 *  com.atlassian.oauth.shared.sal.Functions
 *  com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings
 *  com.atlassian.oauth.shared.sal.PrefixingPluginSettings
 *  com.atlassian.oauth.shared.sal.TokenProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserResolutionException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth.serviceprovider.sal;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth.Token;
import com.atlassian.oauth.event.AccessTokenAddedEvent;
import com.atlassian.oauth.event.AccessTokenRemovedEvent;
import com.atlassian.oauth.event.RequestTokenAddedEvent;
import com.atlassian.oauth.event.RequestTokenRemovedEvent;
import com.atlassian.oauth.serviceprovider.Clock;
import com.atlassian.oauth.serviceprovider.InvalidTokenException;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.StoreException;
import com.atlassian.oauth.shared.sal.Functions;
import com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings;
import com.atlassian.oauth.shared.sal.PrefixingPluginSettings;
import com.atlassian.oauth.shared.sal.TokenProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserResolutionException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class PluginSettingsServiceProviderTokenStore
implements ServiceProviderTokenStore {
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ServiceProviderConsumerStore consumerStore;
    private final UserManager userManager;
    private final Clock clock;
    private final EventPublisher eventPublisher;

    public PluginSettingsServiceProviderTokenStore(PluginSettingsFactory factory, ServiceProviderConsumerStore consumerStore, UserManager userManager, Clock clock, EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.pluginSettingsFactory = Objects.requireNonNull(factory, "factory");
        this.consumerStore = Objects.requireNonNull(consumerStore, "consumerStore");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ServiceProviderToken get(String token) {
        return this.get(token, this.resolveUser());
    }

    ServiceProviderToken get(String token, Function<String, Principal> userResolver) {
        Principal user;
        ServiceProviderTokenProperties props;
        block10: {
            Objects.requireNonNull(token, "token");
            props = this.settings().get(token);
            if (props == null) {
                return null;
            }
            user = null;
            if (props.getUserName() != null) {
                try {
                    user = userResolver.apply(props.getUserName());
                    if (user == null) {
                        this.removeAndNotify(token);
                        throw new InvalidTokenException("Unknown user " + props.getUserName());
                    }
                    break block10;
                }
                catch (UserResolutionException e) {
                    throw new InvalidTokenException("Unknown user " + props.getUserName(), (Throwable)e);
                }
            }
            if (props.isAccessToken()) {
                throw new StoreException("Token '" + token + "' is an access token, but has no user associated with it");
            }
        }
        if (props.isAccessToken()) {
            return ((ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)ServiceProviderToken.newAccessToken((String)token).tokenSecret(props.getTokenSecret())).consumer(this.consumerStore.get(props.getConsumerKey()))).authorizedBy(user).creationTime(props.getCreationTime()).timeToLive(props.getTimeToLive()).properties(props.getProperties())).session(props.getSession()).build();
        }
        ServiceProviderToken.Version version = props.getVersion() == null ? (props.getCallback() == null ? ServiceProviderToken.Version.V_1_0 : ServiceProviderToken.Version.V_1_0_A) : props.getVersion();
        ServiceProviderToken.ServiceProviderTokenBuilder builder = (ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)((ServiceProviderToken.ServiceProviderTokenBuilder)ServiceProviderToken.newRequestToken((String)token).tokenSecret(props.getTokenSecret())).consumer(this.consumerStore.get(props.getConsumerKey()))).callback(props.getCallback()).creationTime(props.getCreationTime()).timeToLive(props.getTimeToLive()).version(version).properties(props.getProperties());
        if (props.getAuthorization() == ServiceProviderToken.Authorization.AUTHORIZED) {
            builder.authorizedBy(user).verifier(props.getVerifier());
        } else if (props.getAuthorization() == ServiceProviderToken.Authorization.DENIED) {
            builder.deniedBy(user);
        }
        return builder.build();
    }

    public Iterable<ServiceProviderToken> getAccessTokensForUser(String username) {
        return this.settings().getUserAccessTokenKeys(username).stream().map(this.toTokens(this.resolveUser())).collect(Collectors.toList());
    }

    public ServiceProviderToken put(ServiceProviderToken token) {
        Objects.requireNonNull(token, "token");
        this.settings().put(token.getToken(), new ServiceProviderTokenProperties(token));
        return token;
    }

    public void removeAndNotify(String token) {
        Objects.requireNonNull(token, "token");
        this.settings().remove(token);
    }

    public void removeExpiredTokensAndNotify() {
        this.removeTokens(this.hasExpired());
    }

    public void removeExpiredSessionsAndNotify() {
        this.removeTokens(this.hasExpiredSession());
    }

    private void removeTokens(Predicate<ServiceProviderToken> p) {
        Settings settings = this.settings();
        List<ServiceProviderToken> tokens = settings.getTokenKeys(settings.validTokenReferences()).stream().map(this.toTokens(this.doNotResolveUser())).filter(p).collect(Collectors.toList());
        tokens.forEach(token -> settings.remove(token.getToken()));
    }

    public void removeByConsumer(String consumerKey) {
        Settings settings = this.settings();
        List<ServiceProviderToken> tokens = settings.getConsumerTokens(consumerKey, settings.validTokenReferences()).stream().map(this.toTokens(this.doNotResolveUser())).collect(Collectors.toList());
        tokens.forEach(token -> settings.remove(token.getToken()));
    }

    private Function<String, Principal> resolveUser() {
        return username -> {
            try {
                return this.userManager.resolve(username);
            }
            catch (UserResolutionException e) {
                throw new InvalidTokenException("Unknown user " + username, (Throwable)e);
            }
        };
    }

    private Function<String, Principal> doNotResolveUser() {
        return username -> () -> username;
    }

    private Function<String, ServiceProviderToken> toTokens(Function<String, Principal> userResolver) {
        return new KeyToToken(userResolver);
    }

    private Predicate<ServiceProviderToken> hasExpired() {
        return new HasExpired(this.clock);
    }

    private Predicate<ServiceProviderToken> hasExpiredSession() {
        return new HasExpiredSession(this.clock);
    }

    private Settings settings() {
        return new Settings(this.pluginSettingsFactory.createGlobalSettings(), this.eventPublisher);
    }

    static final class ServiceProviderTokenProperties
    extends TokenProperties {
        static final String AUTHORIZATION = "authorization";
        static final String USER_NAME = "userName";
        static final String VERIFIER = "verifier";
        static final String CALLBACK = "callback";
        static final String CREATION_TIME = "creationTime";
        static final String TIME_TO_LIVE = "timeToLive";
        static final String VERSION = "version";
        static final String SESSION_HANDLE = "session.handle";
        static final String SESSION_CREATION_TIME = "session.creationTime";
        static final String SESSION_LAST_RENEWAL_TIME = "session.lastRenewalTime";
        static final String SESSION_TIME_TO_LIVE = "session.timeToLive";

        public ServiceProviderTokenProperties(Properties properties) {
            super(properties);
        }

        public ServiceProviderTokenProperties(ServiceProviderToken token) {
            super((Token)token);
            this.putAuthorization(token.getAuthorization());
            if (token.getUser() != null) {
                this.putUserName(token.getUser().getName());
            }
            this.putVerifier(token.getVerifier());
            this.putCallback(token.getCallback());
            this.putCreationTime(token.getCreationTime());
            this.putTimeToLive(token.getTimeToLive());
            this.putVersion(token.getVersion());
            this.putSession(token.getSession());
        }

        public ServiceProviderToken.Authorization getAuthorization() {
            String authz = this.get(AUTHORIZATION);
            if (authz != null) {
                return ServiceProviderToken.Authorization.valueOf((String)authz);
            }
            return this.getUserName() != null ? ServiceProviderToken.Authorization.AUTHORIZED : ServiceProviderToken.Authorization.NONE;
        }

        public void putAuthorization(ServiceProviderToken.Authorization authz) {
            this.put(AUTHORIZATION, authz.name());
        }

        public String getUserName() {
            return this.get(USER_NAME);
        }

        private void putUserName(String name) {
            this.put(USER_NAME, name);
        }

        public String getVerifier() {
            return this.get(VERIFIER);
        }

        private void putVerifier(String verifier) {
            this.put(VERIFIER, verifier);
        }

        public URI getCallback() {
            String callback = this.get(CALLBACK);
            if (callback == null) {
                return null;
            }
            try {
                return new URI(callback);
            }
            catch (URISyntaxException e) {
                throw new StoreException("Invalid callback", (Throwable)e);
            }
        }

        private void putCallback(URI callback) {
            if (callback == null) {
                return;
            }
            this.put(CALLBACK, callback.toString());
        }

        public long getCreationTime() {
            return Long.parseLong(this.get(CREATION_TIME));
        }

        private void putCreationTime(long creationTime) {
            this.put(CREATION_TIME, Long.toString(creationTime));
        }

        public long getTimeToLive() {
            return Long.parseLong(this.get(TIME_TO_LIVE));
        }

        private void putTimeToLive(long timeToLive) {
            this.put(TIME_TO_LIVE, Long.toString(timeToLive));
        }

        public ServiceProviderToken.Version getVersion() {
            String version = this.get(VERSION);
            if (version == null) {
                return null;
            }
            return ServiceProviderToken.Version.valueOf((String)version);
        }

        private void putVersion(ServiceProviderToken.Version version) {
            if (version != null) {
                this.put(VERSION, version.name());
            }
        }

        public ServiceProviderToken.Session getSession() {
            String handle = this.get(SESSION_HANDLE);
            if (handle == null) {
                return null;
            }
            return ServiceProviderToken.Session.newSession((String)handle).creationTime(Long.parseLong(this.get(SESSION_CREATION_TIME))).lastRenewalTime(Long.parseLong(this.get(SESSION_LAST_RENEWAL_TIME))).timeToLive(Long.parseLong(this.get(SESSION_TIME_TO_LIVE))).build();
        }

        private void putSession(ServiceProviderToken.Session session) {
            if (session != null) {
                this.put(SESSION_HANDLE, session.getHandle());
                this.put(SESSION_CREATION_TIME, Long.toString(session.getCreationTime()));
                this.put(SESSION_LAST_RENEWAL_TIME, Long.toString(session.getLastRenewalTime()));
                this.put(SESSION_TIME_TO_LIVE, Long.toString(session.getTimeToLive()));
            }
        }
    }

    static final class Settings {
        static final String TOKEN_KEYS = "tokenKeys";
        static final String TOKEN_PREFIX = "token";
        static final String KEY_LIST_PROPERTY = "keys";
        static final String USER_ACCESS_TOKENS = "userAccessTokens";
        static final String USER_ACCESS_TOKENS_USERNAME_PROPERTY = "username";
        static final String CONSUMER_TOKENS = "consumerTokens";
        static final String CONSUMER_TOKENS_CONSUMER_KEY_PROPERTY = "consumerKey";
        private final PluginSettings settings;
        private final Predicate<String> isValidTokenReference;
        private final EventPublisher eventPublisher;

        Settings(PluginSettings settings, EventPublisher eventPublisher) {
            this.settings = new PrefixingPluginSettings((PluginSettings)new HashingLongPropertyKeysPluginSettings(settings), ServiceProviderTokenStore.class.getName());
            this.isValidTokenReference = new IsValidTokenReference(this.settings);
            this.eventPublisher = eventPublisher;
        }

        ServiceProviderTokenProperties get(String token) {
            Properties props = (Properties)this.settings.get("token." + token);
            if (props == null) {
                return null;
            }
            return new ServiceProviderTokenProperties(props);
        }

        void put(String token, ServiceProviderTokenProperties tokenProperties) {
            RequestTokenAddedEvent event;
            this.settings.put("token." + token, (Object)tokenProperties.asProperties());
            this.addTokenKey(token);
            this.addConsumerToken(tokenProperties.getConsumerKey(), token);
            if (tokenProperties.isAccessToken()) {
                this.addUserAccessToken(tokenProperties.getUserName(), token);
                event = new AccessTokenAddedEvent(tokenProperties.getUserName(), tokenProperties.getConsumerKey());
            } else {
                event = new RequestTokenAddedEvent(tokenProperties.getUserName(), tokenProperties.getConsumerKey());
            }
            this.eventPublisher.publish((Object)event);
        }

        void remove(String token) {
            RequestTokenRemovedEvent event;
            ServiceProviderTokenProperties tokenProperties = this.get(token);
            if (tokenProperties == null) {
                return;
            }
            this.settings.remove("token." + token);
            this.removeTokenKey(token);
            this.removeConsumerToken(tokenProperties.getConsumerKey(), token);
            String userName = tokenProperties.getUserName();
            if (tokenProperties.isAccessToken()) {
                this.removeUserAccessToken(userName, token);
                event = new AccessTokenRemovedEvent(userName, tokenProperties.getConsumerKey());
            } else {
                event = new RequestTokenRemovedEvent(userName, tokenProperties.getConsumerKey());
            }
            this.eventPublisher.publish((Object)event);
        }

        Set<String> getTokenKeys(Predicate<String> tokenReferenceValidator) {
            return this.getTokenKeySet(TOKEN_KEYS, tokenReferenceValidator);
        }

        private void putTokenKeys(Iterable<String> tokenKeys) {
            this.putTokenKeySet(TOKEN_KEYS, tokenKeys);
        }

        private void addTokenKey(String token) {
            Set<String> tokenKeys = this.getTokenKeys(this.validTokenReferences());
            tokenKeys.add(token);
            this.putTokenKeys(tokenKeys);
        }

        private void removeTokenKey(String token) {
            Set<String> tokenKeys = this.getTokenKeys(this.allTokenReferences());
            tokenKeys.remove(token);
            this.putTokenKeys(tokenKeys);
        }

        Set<String> getUserAccessTokenKeys(String user) {
            return this.getTokenKeySet("userAccessTokens." + user, this.validTokenReferences());
        }

        private void putUserAccessTokens(String user, Set<String> tokenKeys) {
            this.putTokenKeySet("userAccessTokens." + user, tokenKeys, USER_ACCESS_TOKENS_USERNAME_PROPERTY, user);
        }

        private void addUserAccessToken(String userName, String tokenKey) {
            Set<String> tokenKeys = this.getUserAccessTokenKeys(userName);
            tokenKeys.add(tokenKey);
            this.putUserAccessTokens(userName, tokenKeys);
        }

        private void removeUserAccessToken(String userName, String tokenKey) {
            Set<String> tokenKeys = this.getUserAccessTokenKeys(userName);
            tokenKeys.remove(tokenKey);
            this.putUserAccessTokens(userName, tokenKeys);
        }

        Set<String> getConsumerTokens(String consumerKey, Predicate<String> tokenReferenceValidator) {
            return this.getTokenKeySet("consumerTokens." + consumerKey, tokenReferenceValidator);
        }

        private void putConsumerTokens(String consumerKey, Iterable<String> tokenKeys) {
            this.putTokenKeySet("consumerTokens." + consumerKey, tokenKeys, CONSUMER_TOKENS_CONSUMER_KEY_PROPERTY, consumerKey);
        }

        private void addConsumerToken(String consumerKey, String tokenKey) {
            Set<String> consumerTokens = this.getConsumerTokens(consumerKey, this.validTokenReferences());
            consumerTokens.add(tokenKey);
            this.putConsumerTokens(consumerKey, consumerTokens);
        }

        private void removeConsumerToken(String consumerKey, String tokenKey) {
            Set<String> consumerTokens = this.getConsumerTokens(consumerKey, this.allTokenReferences());
            consumerTokens.remove(tokenKey);
            this.putConsumerTokens(consumerKey, consumerTokens);
        }

        private Set<String> getTokenKeySet(String setKey, Predicate<String> tokenReferenceValidator) {
            String tokenKeys;
            Object value = this.settings.get(setKey);
            if (value == null) {
                return new HashSet<String>();
            }
            if (value instanceof String) {
                tokenKeys = (String)value;
            } else if (value instanceof Properties) {
                tokenKeys = (String)((Properties)value).get(KEY_LIST_PROPERTY);
            } else {
                throw new IllegalStateException("unexpected value of class " + value.getClass() + " for key " + setKey);
            }
            if (StringUtils.isBlank((CharSequence)tokenKeys)) {
                return new HashSet<String>();
            }
            return Arrays.stream(tokenKeys.split("/")).map(Functions.KEY_DECODER).filter(tokenReferenceValidator).collect(Collectors.toSet());
        }

        private String toDelimitedString(Iterable<String> tokenSet) {
            return StreamSupport.stream(tokenSet.spliterator(), false).map(Functions.KEY_ENCODER).collect(Collectors.joining("/"));
        }

        private void putTokenKeySet(String setKey, Iterable<String> tokenSet) {
            this.settings.put(setKey, (Object)this.toDelimitedString(tokenSet));
        }

        private void putTokenKeySet(String setKey, Iterable<String> tokenSet, String idPropertyName, String idPropertyValue) {
            String keyListString = this.toDelimitedString(tokenSet);
            Properties props = new Properties();
            props.put(KEY_LIST_PROPERTY, keyListString);
            props.put(idPropertyName, idPropertyValue);
            this.settings.put(setKey, (Object)props);
        }

        private Predicate<String> validTokenReferences() {
            return this.isValidTokenReference;
        }

        private Predicate<String> allTokenReferences() {
            return s -> true;
        }

        static final class IsValidTokenReference
        implements Predicate<String> {
            private final PluginSettings settings;

            IsValidTokenReference(PluginSettings settings) {
                this.settings = settings;
            }

            @Override
            public boolean test(String token) {
                return this.settings.get("token." + token) != null;
            }
        }
    }

    private static class HasExpiredSession
    implements Predicate<ServiceProviderToken> {
        private final Clock clock;

        public HasExpiredSession(Clock clock) {
            this.clock = clock;
        }

        @Override
        public boolean test(ServiceProviderToken token) {
            return token.getSession() != null && token.getSession().hasExpired(this.clock);
        }
    }

    private static class HasExpired
    implements Predicate<ServiceProviderToken> {
        private final Clock clock;

        public HasExpired(Clock clock) {
            this.clock = clock;
        }

        @Override
        public boolean test(ServiceProviderToken token) {
            return token.getSession() == null && token.hasExpired(this.clock);
        }
    }

    private class KeyToToken
    implements Function<String, ServiceProviderToken> {
        private final Function<String, Principal> userResolver;

        private KeyToToken(Function<String, Principal> userResolver) {
            this.userResolver = userResolver;
        }

        @Override
        public ServiceProviderToken apply(String tokenKey) {
            return PluginSettingsServiceProviderTokenStore.this.get(tokenKey, this.userResolver);
        }
    }
}

