/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.external.OAuth2ProviderService
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.LocaleResolver
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.oauth.serviceprovider.internal.servlet.user;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.internal.oauth2.OAuth2OsgiServiceFactory;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.external.OAuth2ProviderService;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.LocaleResolver;
import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class AccessTokensServletContext {
    private final LocaleResolver localeResolver;
    private final ApplicationProperties applicationProperties;
    private final ServiceProviderTokenStore store;
    private final OAuth2OsgiServiceFactory oAuth2OsgiServiceFactory;

    public AccessTokensServletContext(LocaleResolver localeResolver, ApplicationProperties applicationProperties, ServiceProviderTokenStore store, OAuth2OsgiServiceFactory oAuth2OsgiServiceFactory) {
        this.localeResolver = Objects.requireNonNull(localeResolver, "localeResolver");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.store = Objects.requireNonNull(store, "store");
        this.oAuth2OsgiServiceFactory = Objects.requireNonNull(oAuth2OsgiServiceFactory, "oAuth2OsgiServiceFactory");
    }

    @Nonnull
    public Map<String, Object> getContext(String username) {
        Locale locale = this.localeResolver.getLocale();
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("tokenItems", this.getTokenRepresentations(username));
        context.put("dateFormat", DateFormat.getDateInstance(2, locale));
        context.put("timeFormat", DateFormat.getTimeInstance(3, locale));
        context.put("productName", StringUtils.capitalize((String)this.applicationProperties.getDisplayName().toLowerCase()));
        context.put("baseUrl", this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
        context.put("scopeDescriptions", this.oAuth2OsgiServiceFactory.getScopeDescriptionService().map(ScopeDescriptionService::getScopeDescriptionsWithTitle).orElse(Collections.emptyMap()));
        return context;
    }

    private List<TokenRepresentation> getTokenRepresentations(String username) {
        return Stream.concat(this.getOAuth1TokenRepresentations(username), this.getOAuth2TokenRepresentations()).collect(Collectors.toList());
    }

    private Stream<TokenRepresentation> getOAuth1TokenRepresentations(String username) {
        return StreamSupport.stream(this.store.getAccessTokensForUser(username).spliterator(), false).map(TokenRepresentation::new);
    }

    private Stream<TokenRepresentation> getOAuth2TokenRepresentations() {
        return this.oAuth2OsgiServiceFactory.getOAuth2ProviderService().map(oAuth2ProviderService -> {
            String revokeUrl = oAuth2ProviderService.getOAuth2AuthorizationServerMetadata().getRevocationEndpoint();
            return this.getOAuth2TokensForClients((OAuth2ProviderService)oAuth2ProviderService).entrySet().stream().flatMap(entry -> ((List)entry.getValue()).stream().map(token -> new TokenRepresentation((Client)entry.getKey(), (RefreshToken)token, revokeUrl)));
        }).orElse(Stream.empty());
    }

    private Map<Client, List<RefreshToken>> getOAuth2TokensForClients(OAuth2ProviderService oAuth2ProviderService) {
        HashMap oAuth2ClientsWithTokens = new HashMap();
        for (RefreshToken token : oAuth2ProviderService.listCurrentUsersRefreshTokens()) {
            if (!oAuth2ClientsWithTokens.containsKey(token.getClientId())) {
                oAuth2ProviderService.findClient(token.getClientId()).ifPresent(client -> {
                    ArrayList<RefreshToken> tokensForClient = new ArrayList<RefreshToken>();
                    tokensForClient.add(token);
                    oAuth2ClientsWithTokens.put(token.getClientId(), Pair.of((Object)client, tokensForClient));
                });
                continue;
            }
            ((List)((Pair)oAuth2ClientsWithTokens.get(token.getClientId())).getRight()).add(token);
        }
        return oAuth2ClientsWithTokens.values().stream().collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public static final class TokenRepresentation {
        private static final String URL_REGEX_PATTERN = "((.*?)(https?://\\S+))";
        private final String token;
        private final String consumerName;
        private final URI consumerUri;
        private final Date authorizationDate;
        private final String[] scopes;
        private final String revokeUrl;
        private final boolean oauth2;

        TokenRepresentation(ServiceProviderToken token) {
            this.token = token.getToken();
            this.consumerName = this.resolveConsumerName(token);
            this.consumerUri = TokenRepresentation.parseUriFromDescription(token.getConsumer().getDescription());
            this.authorizationDate = new Date(token.getCreationTime());
            this.scopes = null;
            this.revokeUrl = null;
            this.oauth2 = false;
        }

        private static URI parseUriFromDescription(String description) {
            if (StringUtils.isEmpty((CharSequence)description)) {
                return null;
            }
            Pattern p = Pattern.compile(URL_REGEX_PATTERN);
            Matcher m = p.matcher(description.trim());
            if (!m.matches()) {
                return null;
            }
            try {
                return URI.create(m.group(3));
            }
            catch (IllegalArgumentException e) {
                return null;
            }
        }

        TokenRepresentation(Client client, RefreshToken token, String revokeUrl) {
            this.token = token.getId();
            this.consumerName = client.getName();
            this.consumerUri = URI.create((String)client.getRedirects().get(0));
            this.authorizationDate = new Date(token.getAuthorizationDate());
            this.scopes = this.getScopes(token.getScope());
            this.revokeUrl = revokeUrl + "/" + token.getId();
            this.oauth2 = true;
        }

        private String[] getScopes(Scope scope) {
            List<String> scopes = scope.getScopeAndInheritedScopes().stream().map(Scope::getName).collect(Collectors.toList());
            this.filterOutAdminIfSystemAdminPresent(scopes);
            this.lowestScopeFirst(scopes);
            return scopes.toArray(new String[0]);
        }

        private void filterOutAdminIfSystemAdminPresent(List<String> scopes) {
            if (scopes.contains("SYSTEM_ADMIN")) {
                scopes.remove("ADMIN_WRITE");
                scopes.remove("ADMIN");
            }
        }

        private void lowestScopeFirst(List<String> scopes) {
            Collections.reverse(scopes);
        }

        private String resolveConsumerName(ServiceProviderToken token) {
            if (token.hasProperty("alternate.consumer.name")) {
                return token.getProperty("alternate.consumer.name");
            }
            return token.getConsumer().getName();
        }

        public String getToken() {
            return this.token;
        }

        public String getConsumerName() {
            return this.consumerName;
        }

        public String getConsumerHostName() {
            URI uri = this.getConsumerUri();
            return uri == null ? null : uri.getHost();
        }

        public URI getConsumerUri() {
            return this.consumerUri;
        }

        public Date getCreationTime() {
            return this.authorizationDate;
        }

        public String[] getScopes() {
            return this.scopes;
        }

        public String getRevokeUrl() {
            return this.revokeUrl;
        }

        public boolean isOauth2() {
            return this.oauth2;
        }
    }
}

