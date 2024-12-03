/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor
 *  com.atlassian.bamboo.setup.BambooHomeLocator
 *  com.atlassian.bamboo.user.BambooUserManager
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.bitbucket.auth.AuthenticationService
 *  com.atlassian.bitbucket.user.UserService
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.themes.ColourSchemeManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.jira.config.properties.LookAndFeelBean
 *  com.atlassian.jira.user.UserKeyService
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationService
 *  com.atlassian.oauth2.provider.api.authorization.SupportedResponseType
 *  com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationDao
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.client.dao.ClientDao
 *  com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao
 *  com.atlassian.oauth2.provider.api.external.OAuth2AuthorizationServerMetadata
 *  com.atlassian.oauth2.provider.api.external.OAuth2ProviderService
 *  com.atlassian.oauth2.provider.api.pkce.PkceService
 *  com.atlassian.oauth2.provider.api.settings.JwtSecretInitService
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsService
 *  com.atlassian.oauth2.provider.api.token.AccessTokenAuthenticationHandler
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenDao
 *  com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenDao
 *  com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.user.UserManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.provider.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.api.AuditService;
import com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor;
import com.atlassian.bamboo.setup.BambooHomeLocator;
import com.atlassian.bamboo.user.BambooUserManager;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.bitbucket.auth.AuthenticationService;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.properties.LookAndFeelBean;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.oauth2.common.IdGenerator;
import com.atlassian.oauth2.common.validator.HttpsValidator;
import com.atlassian.oauth2.common.web.loopsprevention.NoopRedirectsLoopPreventer;
import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.oauth2.common.web.loopsprevention.SeraphEnabledCondition;
import com.atlassian.oauth2.common.web.loopsprevention.SeraphRedirectsLoopPreventer;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationService;
import com.atlassian.oauth2.provider.api.authorization.SupportedResponseType;
import com.atlassian.oauth2.provider.api.authorization.dao.AuthorizationDao;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.client.dao.ClientDao;
import com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao;
import com.atlassian.oauth2.provider.api.external.OAuth2AuthorizationServerMetadata;
import com.atlassian.oauth2.provider.api.external.OAuth2ProviderService;
import com.atlassian.oauth2.provider.api.pkce.PkceService;
import com.atlassian.oauth2.provider.api.settings.JwtSecretInitService;
import com.atlassian.oauth2.provider.api.settings.ProviderSettingsDao;
import com.atlassian.oauth2.provider.api.settings.ProviderSettingsService;
import com.atlassian.oauth2.provider.api.token.AccessTokenAuthenticationHandler;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.access.dao.AccessTokenDao;
import com.atlassian.oauth2.provider.api.token.refresh.dao.RefreshTokenDao;
import com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator;
import com.atlassian.oauth2.provider.core.audit.OAuth2ProviderAuditListener;
import com.atlassian.oauth2.provider.core.authentication.BitbucketLogoutHandler;
import com.atlassian.oauth2.provider.core.authentication.LogoutHandler;
import com.atlassian.oauth2.provider.core.authentication.SeraphLogoutHandler;
import com.atlassian.oauth2.provider.core.authorization.CodeGenerator;
import com.atlassian.oauth2.provider.core.authorization.DefaultAuthorizationService;
import com.atlassian.oauth2.provider.core.authorization.dao.DefaultAuthorizationDao;
import com.atlassian.oauth2.provider.core.client.DefaultClientService;
import com.atlassian.oauth2.provider.core.client.dao.DefaultClientDao;
import com.atlassian.oauth2.provider.core.client.dao.DefaultRedirectUriDao;
import com.atlassian.oauth2.provider.core.credentials.ClientCredentialsGenerator;
import com.atlassian.oauth2.provider.core.event.OAuth2ProviderEventPublisher;
import com.atlassian.oauth2.provider.core.external.DefaultOAuth2ProviderService;
import com.atlassian.oauth2.provider.core.external.OAuth2AuthorizationServerMetadataFactory;
import com.atlassian.oauth2.provider.core.jobs.RemoveExpiredAuthorizationsJob;
import com.atlassian.oauth2.provider.core.jobs.RemoveExpiredTokensJob;
import com.atlassian.oauth2.provider.core.jobs.StatisticsJob;
import com.atlassian.oauth2.provider.core.pkce.DefaultPkceService;
import com.atlassian.oauth2.provider.core.plugin.PluginChecker;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.core.security.Hasher;
import com.atlassian.oauth2.provider.core.security.Sha256Hasher;
import com.atlassian.oauth2.provider.core.settings.DefaultJwtSecretInitService;
import com.atlassian.oauth2.provider.core.settings.DefaultProviderSettingsDao;
import com.atlassian.oauth2.provider.core.settings.DefaultProviderSettingsService;
import com.atlassian.oauth2.provider.core.token.DefaultAccessTokenAuthenticationHandler;
import com.atlassian.oauth2.provider.core.token.DefaultTokenService;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.core.token.access.dao.DefaultAccessTokenDao;
import com.atlassian.oauth2.provider.core.token.refresh.dao.DefaultRefreshTokenDao;
import com.atlassian.oauth2.provider.core.user.BambooUserProvider;
import com.atlassian.oauth2.provider.core.user.BitbucketUserProvider;
import com.atlassian.oauth2.provider.core.user.ConfluenceUserProvider;
import com.atlassian.oauth2.provider.core.user.JiraUserProvider;
import com.atlassian.oauth2.provider.core.user.ProductUserProvider;
import com.atlassian.oauth2.provider.core.user.RefappUserProvider;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationConsentServletConfiguration;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationErrorServletConfiguration;
import com.atlassian.oauth2.provider.core.xsrf.DefaultOAuth2XsrfTokenGenerator;
import com.atlassian.oauth2.provider.core.xsrf.XsrfValidator;
import com.atlassian.oauth2.provider.data.ProviderPluginData;
import com.atlassian.oauth2.provider.data.themes.BambooCustomThemeFactory;
import com.atlassian.oauth2.provider.data.themes.BitbucketCustomThemeFactory;
import com.atlassian.oauth2.provider.data.themes.ConfluenceCustomThemeFactory;
import com.atlassian.oauth2.provider.data.themes.CustomThemeConfiguration;
import com.atlassian.oauth2.provider.data.themes.JiraCustomThemeFactory;
import com.atlassian.oauth2.provider.data.themes.ProductCustomThemeFactory;
import com.atlassian.oauth2.provider.data.themes.RefappCustomThemeFactory;
import com.atlassian.oauth2.provider.plugin.DefaultPluginChecker;
import com.atlassian.oauth2.provider.rest.service.AuthorizationRestService;
import com.atlassian.oauth2.provider.rest.service.ClientRestService;
import com.atlassian.oauth2.provider.rest.service.RevokeTokenService;
import com.atlassian.oauth2.provider.rest.service.ScopeRestService;
import com.atlassian.oauth2.provider.rest.service.TokenRestService;
import com.atlassian.oauth2.provider.rest.service.grant.GrantProcessorFactory;
import com.atlassian.oauth2.provider.rest.validation.AuthorizationValidator;
import com.atlassian.oauth2.provider.rest.validation.RestClientValidator;
import com.atlassian.oauth2.provider.rest.validation.RevokeTokenValidator;
import com.atlassian.oauth2.provider.web.ProviderPluginAuthorizationConsentServletConfiguration;
import com.atlassian.oauth2.provider.web.ProviderPluginAuthorizationErrorServletConfiguration;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BambooOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BitbucketOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2ProviderPluginConfiguration {
    private static final String REST_API_PATH = "/rest/oauth2/latest";

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean(name={"guidGenerator"})
    public IdGenerator idGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    public ClientCredentialsGenerator clientCredentialsGenerator() {
        return new ClientCredentialsGenerator();
    }

    @Bean
    public AccessTokenDao tokenDao(ActiveObjects activeObjects, Clock clock, ScopeResolver scopeResolver) {
        return new DefaultAccessTokenDao(activeObjects, clock, scopeResolver);
    }

    @Bean
    public WebResourceDataProvider webResourceDataProvider(com.atlassian.sal.api.ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver, ScopeDescriptionService scopeDescriptionService) {
        return new ProviderPluginData(applicationProperties, helpPathResolver, scopeDescriptionService);
    }

    @Bean
    public RefreshTokenDao refreshTokenDao(ActiveObjects activeObjects, Clock clock, ScopeResolver scopeResolver) {
        return new DefaultRefreshTokenDao(activeObjects, clock, scopeResolver);
    }

    @Bean
    public Hasher hasher() {
        return new Sha256Hasher();
    }

    @Bean
    public OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher(EventPublisher eventPublisher) {
        return new OAuth2ProviderEventPublisher(eventPublisher);
    }

    @Bean
    public TokenService tokenService(AccessTokenDao accessTokenDao, RefreshTokenDao refreshTokenDao, ClientCredentialsGenerator clientCredentialsGenerator, UserManager userManager, Clock clock, I18nResolver i18nResolver, Hasher hasher, OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher) {
        return new DefaultTokenService(accessTokenDao, refreshTokenDao, clientCredentialsGenerator, userManager, clock, i18nResolver, hasher, oAuth2ProviderEventPublisher);
    }

    @Bean
    public ClientDao clientDao(ActiveObjects activeObjects, ScopeResolver scopeResolver, RedirectUriDao redirectUriDao) {
        return new DefaultClientDao(activeObjects, scopeResolver, redirectUriDao);
    }

    @Bean
    public ClientService clientService(ClientDao clientDao, ClientCredentialsGenerator clientCredentialsGenerator, TokenService tokenService, UserManager userManager, I18nResolver i18nResolver, @Qualifier(value="guidGenerator") IdGenerator idGenerator, OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher, RedirectUriDao redirectUriDao) {
        return new DefaultClientService(clientDao, clientCredentialsGenerator, tokenService, userManager, i18nResolver, idGenerator, oAuth2ProviderEventPublisher, redirectUriDao);
    }

    @Bean
    public OAuth2AuthorizationServerMetadataFactory oAuth2AuthorizationServerMetadataFactory(ScopeResolver scopeResolver, com.atlassian.sal.api.ApplicationProperties applicationProperties) {
        return () -> {
            Set scopesSupported = scopeResolver.getAvailableScopes().stream().map(Scope::getName).collect(Collectors.toSet());
            Set responseTypesSupported = Arrays.stream(SupportedResponseType.values()).map(supportedResponseType -> supportedResponseType.value).collect(Collectors.toSet());
            String baseUrl = applicationProperties.getBaseUrl(UrlMode.CANONICAL);
            return OAuth2AuthorizationServerMetadata.builder().issuer(baseUrl).authorizationEndpoint(baseUrl + REST_API_PATH + "/authorize").tokenEndpoint(baseUrl + REST_API_PATH + "/token").revocationEndpoint(baseUrl + REST_API_PATH + "/revoke").scopesSupported(scopesSupported).responseTypesSupported(responseTypesSupported).responseModesSupported((Set)ImmutableSet.of((Object)"query")).grantTypesSupported((Set)ImmutableSet.of((Object)"authorization_code")).tokenEndpointAuthMethodsSupported((Set)ImmutableSet.of((Object)"client_secret_post")).revocationEndpointAuthMethodsSupported((Set)ImmutableSet.of((Object)"client_secret_post")).build();
        };
    }

    @Bean
    public OAuth2ProviderService providerStorageService(ClientService clientService, TokenService tokenService, UserManager userManager, OAuth2AuthorizationServerMetadataFactory oAuth2AuthorizationServerMetadataFactory) {
        return new DefaultOAuth2ProviderService(clientService, tokenService, userManager, oAuth2AuthorizationServerMetadataFactory);
    }

    @Bean
    public CodeGenerator oauthCodeGenerator(ClientCredentialsGenerator clientCredentialsGenerator) {
        return new CodeGenerator(clientCredentialsGenerator);
    }

    @Bean
    public AuthorizationDao authorizationDao(ActiveObjects activeObjects, Clock clock, ScopeResolver scopeResolver) {
        return new DefaultAuthorizationDao(activeObjects, clock, scopeResolver);
    }

    @Bean
    public AuthorizationService authorizationService(CodeGenerator codeGenerator, UserManager userManager, AuthorizationDao authorizationDao, Clock clock, PkceService pkceService, EventPublisher eventPublisher) {
        return new DefaultAuthorizationService(codeGenerator, userManager, authorizationDao, clock, pkceService, eventPublisher);
    }

    @Bean
    public RedirectUriDao redirectUriDao(ActiveObjects activeObjects) {
        return new DefaultRedirectUriDao(activeObjects);
    }

    @Bean
    @Conditional(value={BitbucketOnly.class})
    public RedirectsLoopPreventer bitbucketRedirectsLoopPreventer() {
        return new NoopRedirectsLoopPreventer();
    }

    @Bean
    @Conditional(value={SeraphEnabledCondition.class})
    public RedirectsLoopPreventer seraphRedirectsLoopPreventer() {
        return new SeraphRedirectsLoopPreventer();
    }

    @Bean
    @Conditional(value={SeraphEnabledCondition.class})
    public LogoutHandler seraphLogoutHandler() {
        return new SeraphLogoutHandler();
    }

    @Bean
    public HttpsValidator httpsValidator(com.atlassian.sal.api.ApplicationProperties applicationProperties) {
        return new HttpsValidator(applicationProperties, SystemProperty.DEV_MODE, SystemProperty.SKIP_BASE_URL_HTTPS_REQUIREMENT);
    }

    @Bean
    public RestClientValidator restClientValidator(I18nResolver i18nResolver, UserManager userManager, ClientService clientService, ScopeResolver scopeResolver) {
        return new RestClientValidator(i18nResolver, userManager, clientService, scopeResolver);
    }

    @Bean
    public ScopeRestService scopeRestService(ScopeResolver scopeResolver) {
        return new ScopeRestService(scopeResolver);
    }

    @Bean
    public ClientRestService clientRestService(RestClientValidator providerRestValidator, ClientService clientService, ScopeResolver scopeResolver, I18nResolver i18nResolver) {
        return new ClientRestService(providerRestValidator, clientService, scopeResolver, i18nResolver);
    }

    @Bean
    public AuthorizationConsentServletConfiguration authorizationConsentTemplateConfiguration(com.atlassian.sal.api.ApplicationProperties applicationProperties) {
        return new ProviderPluginAuthorizationConsentServletConfiguration(applicationProperties);
    }

    @Bean
    public AuthorizationErrorServletConfiguration authorizationErrorServletConfiguration(com.atlassian.sal.api.ApplicationProperties applicationProperties) {
        return new ProviderPluginAuthorizationErrorServletConfiguration(applicationProperties);
    }

    @Bean
    public AuthorizationRestService authorizationRestService(AuthorizationService authorizationService, ClientService clientService, AuthorizationValidator authorizationValidator, XsrfValidator xsrfValidator, ScopeRestService scopeRestService, ScopeResolver scopeResolver, AuthorizationConsentServletConfiguration authorizationConsentServletConfiguration, AuthorizationErrorServletConfiguration authorizationErrorServletConfiguration) {
        return new AuthorizationRestService(authorizationService, clientService, authorizationValidator, xsrfValidator, scopeRestService, scopeResolver, authorizationConsentServletConfiguration, authorizationErrorServletConfiguration);
    }

    @Bean
    public GrantProcessorFactory grantTypeFactory(AuthorizationService authorizationService, TokenService tokenService, ClientService clientService, I18nResolver i18nResolver, ClusterLockService clusterLockService, JwtService jwtService, PkceService pkceService) {
        return new GrantProcessorFactory(authorizationService, tokenService, clientService, i18nResolver, clusterLockService, jwtService, pkceService);
    }

    @Bean
    public RevokeTokenValidator revokeTokenValidator(TokenService tokenService, ClientService clientService, I18nResolver i18nResolver, JwtService jwtService) {
        return new RevokeTokenValidator(tokenService, clientService, i18nResolver, jwtService);
    }

    @Bean
    public RevokeTokenService revokeTokenService(TokenService tokenService, RevokeTokenValidator revokeTokenValidator, JwtService jwtService, I18nResolver i18nResolver, OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher) {
        return new RevokeTokenService(tokenService, revokeTokenValidator, jwtService, i18nResolver, oAuth2ProviderEventPublisher);
    }

    @Bean
    public TokenRestService tokenRestService(GrantProcessorFactory grantProcessorFactory) {
        return new TokenRestService(grantProcessorFactory);
    }

    @Bean
    public AuthorizationValidator authorizationValidator(I18nResolver i18nResolver, ScopeResolver scopeResolver, PkceService pkceService, ClientService clientService) {
        return new AuthorizationValidator(i18nResolver, scopeResolver, pkceService, clientService);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public PluginChecker pluginChecker(EventPublisher eventPublisher, PluginAccessor pluginAccessor) {
        return new DefaultPluginChecker(eventPublisher, pluginAccessor);
    }

    @Bean
    public RemoveExpiredAuthorizationsJob removeExpiredAuthorizationsJob(SchedulerService schedulerService, AuthorizationService authorizationService, PluginChecker pluginChecker) {
        return new RemoveExpiredAuthorizationsJob(schedulerService, authorizationService, pluginChecker);
    }

    @Bean
    public RemoveExpiredTokensJob removeExpiredRefreshTokensJob(SchedulerService schedulerService, TokenService tokenService, PluginChecker pluginChecker) {
        return new RemoveExpiredTokensJob(schedulerService, tokenService, pluginChecker);
    }

    @Bean
    public StatisticsJob statisticsJob(SchedulerService schedulerService, com.atlassian.sal.api.ApplicationProperties applicationProperties, ClientService clientService, TokenService tokenService, EventPublisher eventPublisher) {
        return new StatisticsJob(schedulerService, applicationProperties, clientService, tokenService, eventPublisher);
    }

    @Bean
    public AccessTokenAuthenticationHandler accessTokenHandler(TokenService tokenService, Clock clock, AuthenticationListener authenticationListener, I18nResolver i18nResolver, ProductUserProvider productUserProvider, JwtService jwtService) {
        return new DefaultAccessTokenAuthenticationHandler(tokenService, clock, authenticationListener, i18nResolver, productUserProvider, jwtService);
    }

    @Bean
    public OAuth2XsrfTokenGenerator oAuth2XsrfTokenGenerator(I18nResolver i18nResolver) {
        return new DefaultOAuth2XsrfTokenGenerator(i18nResolver);
    }

    @Bean
    public XsrfValidator xsrfValidatator(OAuth2XsrfTokenGenerator oAuth2XsrfTokenGenerator, I18nResolver i18nResolver) {
        return new XsrfValidator(oAuth2XsrfTokenGenerator, i18nResolver);
    }

    @Bean
    public ProviderSettingsDao providerSettingsDao(PluginSettingsFactory pluginSettingsFactory, ClusterLockService clusterLockService, ClientCredentialsGenerator clientCredentialsGenerator) {
        return new DefaultProviderSettingsDao(pluginSettingsFactory, clusterLockService, clientCredentialsGenerator);
    }

    @Bean
    public ProviderSettingsService providerSettingsService(ProviderSettingsDao providerSettingsDao, CacheFactory cacheFactory) {
        return new DefaultProviderSettingsService(providerSettingsDao, cacheFactory);
    }

    @Bean
    public JwtSecretInitService jwtSecretInitService(ProviderSettingsDao providerSettingsDao) {
        return new DefaultJwtSecretInitService(providerSettingsDao);
    }

    @Bean
    public JwtService jwtService(ProviderSettingsService providerSettingsService, I18nResolver i18nResolver) {
        return new JwtService(providerSettingsService, i18nResolver);
    }

    @Bean
    public OAuth2ProviderAuditListener oAuth2ProviderAuditListener(AuditService auditService, EventPublisher eventPublisher, ProductUserProvider productUserProvider) {
        return new OAuth2ProviderAuditListener(auditService, eventPublisher, productUserProvider);
    }

    @Bean
    public PkceService pkceService() {
        return new DefaultPkceService();
    }

    @Configuration
    @Conditional(value={RefappOnly.class})
    public static class RefappConfiguration {
        @Bean
        public ProductUserProvider refappUserProvider(com.atlassian.user.UserManager atlassianUserManager) {
            return new RefappUserProvider(atlassianUserManager);
        }

        @Bean
        public ProductCustomThemeFactory refappCustomThemeFactory() {
            return new RefappCustomThemeFactory();
        }

        @Bean
        public CustomThemeConfiguration customThemeConfiguration() {
            return new CustomThemeConfiguration(new RefappCustomThemeFactory());
        }
    }

    @Configuration
    @Conditional(value={BambooOnly.class})
    public static class BambooConfiguration {
        @Bean
        public ProductUserProvider bambooUserProvider(BambooUserManager bambooUserManager) {
            return new BambooUserProvider(bambooUserManager);
        }

        @Bean
        public ProductCustomThemeFactory bambooCustomThemeFactory(com.atlassian.sal.api.ApplicationProperties applicationProperties, BambooHomeLocator bambooHomeLocator, AdministrationConfigurationAccessor administrationConfigurationAccessor) {
            return new BambooCustomThemeFactory(applicationProperties, bambooHomeLocator, administrationConfigurationAccessor);
        }

        @Bean
        public CustomThemeConfiguration customThemeConfiguration(com.atlassian.sal.api.ApplicationProperties applicationProperties, BambooHomeLocator bambooHomeLocator, AdministrationConfigurationAccessor administrationConfigurationAccessor) {
            return new CustomThemeConfiguration(new BambooCustomThemeFactory(applicationProperties, bambooHomeLocator, administrationConfigurationAccessor));
        }
    }

    @Configuration
    @Conditional(value={BitbucketOnly.class})
    public static class BitbucketConfiguration {
        @Bean
        public ProductUserProvider bitbucketUserProvider(UserService userService) {
            return new BitbucketUserProvider(userService);
        }

        @Bean
        public ProductCustomThemeFactory bitbucketCustomThemeFactory() {
            return new BitbucketCustomThemeFactory();
        }

        @Bean
        public LogoutHandler bitbucketLogoutHandler(AuthenticationService authenticationService) {
            return new BitbucketLogoutHandler(authenticationService);
        }

        @Bean
        public CustomThemeConfiguration customThemeConfiguration() {
            return new CustomThemeConfiguration(new BitbucketCustomThemeFactory());
        }
    }

    @Configuration
    @Conditional(value={ConfluenceOnly.class})
    public static class ConfluenceConfiguration {
        @Bean
        public ProductUserProvider confluenceUserProvider(UserAccessor userAccessor) {
            return new ConfluenceUserProvider(userAccessor);
        }

        @Bean
        public ProductCustomThemeFactory confluenceCustomThemeFactory(ColourSchemeManager colourSchemeManager) {
            return new ConfluenceCustomThemeFactory(colourSchemeManager);
        }

        @Bean
        public CustomThemeConfiguration customThemeConfiguration(ColourSchemeManager colourSchemeManager) {
            return new CustomThemeConfiguration(new ConfluenceCustomThemeFactory(colourSchemeManager));
        }
    }

    @Configuration
    @Conditional(value={JiraOnly.class})
    public static class JiraConfiguration {
        @Bean
        public ProductUserProvider jiraUserProvider(UserKeyService userKeyService, com.atlassian.jira.user.util.UserManager userManager) {
            return new JiraUserProvider(userKeyService, userManager);
        }

        @Bean
        public ProductCustomThemeFactory jiraCustomThemeFactory(ApplicationProperties jiraApplicationProperties, LookAndFeelBean lookAndFeelBean) {
            return new JiraCustomThemeFactory(jiraApplicationProperties, lookAndFeelBean);
        }

        @Bean
        public CustomThemeConfiguration customThemeConfiguration(ApplicationProperties applicationProperties, LookAndFeelBean lookAndFeelBean) {
            return new CustomThemeConfiguration(new JiraCustomThemeFactory(applicationProperties, lookAndFeelBean));
        }
    }
}

