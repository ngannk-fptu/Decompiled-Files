/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.lib.flow.FlowRequestService
 *  com.atlassian.oauth2.client.api.lib.token.TokenService
 *  com.atlassian.oauth2.client.api.storage.TokenHandler
 *  com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService
 *  com.atlassian.oauth2.client.api.storage.config.ProviderType
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.client.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.DefaultRedirectUriSuffixGenerator;
import com.atlassian.oauth2.client.RedirectUriSuffixGenerator;
import com.atlassian.oauth2.client.analytics.StatisticsCollectionService;
import com.atlassian.oauth2.client.api.lib.flow.FlowRequestService;
import com.atlassian.oauth2.client.api.lib.token.TokenService;
import com.atlassian.oauth2.client.api.storage.TokenHandler;
import com.atlassian.oauth2.client.api.storage.config.ClientConfigStorageService;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.oauth2.client.jobs.TokenPruningJob;
import com.atlassian.oauth2.client.lib.flow.RedirectUrlResolver;
import com.atlassian.oauth2.client.lib.flow.ServletFlowRequestService;
import com.atlassian.oauth2.client.lib.flow.SessionBasedFlowRequestService;
import com.atlassian.oauth2.client.lib.token.DefaultRefreshTokenExpirationHandler;
import com.atlassian.oauth2.client.lib.token.DefaultTokenService;
import com.atlassian.oauth2.client.lib.token.InternalTokenService;
import com.atlassian.oauth2.client.lib.token.RefreshTokenExpirationHandler;
import com.atlassian.oauth2.client.lib.web.AuthorizationCodeFlowServlet;
import com.atlassian.oauth2.client.lib.web.AuthorizationCodeFlowUrlsProvider;
import com.atlassian.oauth2.client.properties.SystemProperty;
import com.atlassian.oauth2.client.rest.resource.ClientConfigurationResource;
import com.atlassian.oauth2.client.rest.resource.validator.ClientConfigurationValidator;
import com.atlassian.oauth2.client.rest.resource.validator.DefaultClientConfigurationValidator;
import com.atlassian.oauth2.client.storage.DefaultTokenHandler;
import com.atlassian.oauth2.client.storage.config.DefaultClientConfigStorageService;
import com.atlassian.oauth2.client.storage.config.dao.ClientConfigStore;
import com.atlassian.oauth2.client.storage.config.dao.ClientConfigStoreImpl;
import com.atlassian.oauth2.client.storage.token.DefaultClientTokenStorageService;
import com.atlassian.oauth2.client.storage.token.dao.ClientTokenStore;
import com.atlassian.oauth2.client.storage.token.dao.ClientTokenStoreImpl;
import com.atlassian.oauth2.client.util.ClientHttpsValidator;
import com.atlassian.oauth2.common.IdGenerator;
import com.atlassian.oauth2.common.web.loopsprevention.NoopRedirectsLoopPreventer;
import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.oauth2.common.web.loopsprevention.SeraphEnabledCondition;
import com.atlassian.oauth2.common.web.loopsprevention.SeraphRedirectsLoopPreventer;
import com.atlassian.oauth2.data.PluginDataProvider;
import com.atlassian.oauth2.servlet.TemplateServlet;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BitbucketOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2ClientPluginConfiguration {
    private static final Clock UTC_CLOCK = Clock.systemUTC();
    private static final Set<ProviderType> REQUIRED_REFRESH_TOKENS_PROVIDERS = ImmutableSet.of((Object)ProviderType.GOOGLE, (Object)ProviderType.MICROSOFT);

    @Bean(name={"flowRequestIdGenerator"})
    public IdGenerator flowRequestIdGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean(name={"dbIdGenerator"})
    public IdGenerator idGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean(name={"stateGenerator"})
    public IdGenerator stateGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    public ClientHttpsValidator httpsValidator(ApplicationProperties applicationProperties) {
        return new ClientHttpsValidator(applicationProperties);
    }

    @Bean
    public Clock clock() {
        return UTC_CLOCK;
    }

    @Bean
    public AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider(ApplicationProperties applicationProperties, ClientHttpsValidator clientHttpsValidator, RedirectUriSuffixGenerator redirectUriSuffixGenerator) {
        return new AuthorizationCodeFlowUrlsProvider(applicationProperties, clientHttpsValidator, redirectUriSuffixGenerator);
    }

    @Bean
    public RedirectUrlResolver redirectUrlResolver(AuthorizationCodeFlowUrlsProvider urlsProvider) {
        return flowRequestId -> urlsProvider.getInitFlowUrl(flowRequestId).toString();
    }

    @Bean
    public AuthorizationCodeFlowServlet authorizationCodeFlowServlet(ServletFlowRequestService servletFlowRequestService, InternalTokenService tokenService, AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider) {
        return new AuthorizationCodeFlowServlet(servletFlowRequestService, tokenService, authorizationCodeFlowUrlsProvider);
    }

    @Bean
    public ClientConfigStore clientConfigStore(ActiveObjects activeObjects, @Qualifier(value="flowRequestIdGenerator") IdGenerator idGenerator, ClientHttpsValidator clientHttpsValidator) {
        return new ClientConfigStoreImpl(activeObjects, idGenerator, clientHttpsValidator);
    }

    @Bean
    public ClientTokenStore clientTokenStore(ActiveObjects activeObjects, @Qualifier(value="flowRequestIdGenerator") IdGenerator idGenerator) {
        return new ClientTokenStoreImpl(activeObjects, idGenerator);
    }

    @Bean
    public RedirectUriSuffixGenerator redirectUriSuffixGenerator() {
        return new DefaultRedirectUriSuffixGenerator();
    }

    @Bean
    public RefreshTokenExpirationHandler refreshTokenExpirationHandler() {
        return new DefaultRefreshTokenExpirationHandler(SystemProperty.DEFAULT_REFRESH_TOKEN_DURATION.getValue());
    }

    @Bean
    public WebResourceDataProvider webResourceDataProvider(UserManager userManager, ClientHttpsValidator clientHttpsValidator, ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver) {
        return new PluginDataProvider(userManager, clientHttpsValidator, applicationProperties, helpPathResolver);
    }

    @Bean
    public ClientTokenStorageService clientTokenStorageService(ClientTokenStore clientTokenStore, EventPublisher eventPublisher) {
        return new DefaultClientTokenStorageService(clientTokenStore, eventPublisher);
    }

    @Bean
    public ClientConfigStorageService clientConfigStorageService(ClientConfigStore clientConfigStore, ClientTokenStore clientTokenStore, EventPublisher eventPublisher) {
        return new DefaultClientConfigStorageService(clientConfigStore, clientTokenStore, eventPublisher);
    }

    @Bean
    public DefaultTokenService tokenService(Clock clock, RefreshTokenExpirationHandler refreshTokenExpirationHandler, AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider) {
        return new DefaultTokenService(clock, refreshTokenExpirationHandler, authorizationCodeFlowUrlsProvider, SystemProperty.MINIMUM_ACCESS_TOKEN_ONLY_LIFETIME.getValue(), SystemProperty.MAX_SERVER_TIMEOUT.getValue(), REQUIRED_REFRESH_TOKENS_PROVIDERS);
    }

    @Bean
    public SessionBasedFlowRequestService flowRequestService(RedirectUrlResolver redirectUrlResolver, @Qualifier(value="flowRequestIdGenerator") IdGenerator idGenerator, @Qualifier(value="stateGenerator") IdGenerator stateGenerator, ClientHttpsValidator clientHttpsValidator, Clock clock, EventPublisher eventPublisher) {
        return new SessionBasedFlowRequestService(redirectUrlResolver, idGenerator, stateGenerator, clientHttpsValidator, clock, eventPublisher);
    }

    @Bean
    public TokenHandler tokenHandler(ClientTokenStorageService clientTokenStorageService, ClientConfigStorageService clientConfigStorageService, TokenService tokenService, Clock clock, EventPublisher eventPublisher) {
        return new DefaultTokenHandler(clientTokenStorageService, clientConfigStorageService, tokenService, clock, SystemProperty.UNRECOVERABLE_TOKEN_FAILING_PERIOD.getValue(), eventPublisher);
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
    public TemplateServlet templateServlet(LoginUriProvider loginUriProvider, UserManager userManager, SoyTemplateRenderer soyTemplateRenderer, RedirectsLoopPreventer redirectsLoopPreventer, WebSudoManager webSudoManager, ApplicationProperties applicationProperties) {
        return new TemplateServlet(loginUriProvider, userManager, soyTemplateRenderer, redirectsLoopPreventer, webSudoManager, applicationProperties);
    }

    @Bean
    public TokenPruningJob expiredTokensPruningJob(SchedulerService schedulerService, ClientTokenStore clientTokenStore, Clock clock, EventPublisher eventPublisher) {
        return new TokenPruningJob(schedulerService, clientTokenStore, clock, eventPublisher);
    }

    @Bean
    public StatisticsCollectionService statisticsCollectionService(ClientConfigStore clientConfigStore, ClientTokenStore clientTokenStore, ApplicationProperties applicationProperties, EventPublisher eventPublisher, SchedulerService schedulerService) {
        return new StatisticsCollectionService(clientConfigStore, clientTokenStore, applicationProperties, eventPublisher, schedulerService);
    }

    @Bean
    public ClientConfigurationValidator clientConfigurationValidator(ClientConfigStorageService clientConfigStorageService, I18nResolver i18nResolver, ClientHttpsValidator clientHttpsValidator, RedirectUriSuffixGenerator redirectUriSuffixGenerator) {
        return new DefaultClientConfigurationValidator(clientConfigStorageService, i18nResolver, clientHttpsValidator, redirectUriSuffixGenerator);
    }

    @Bean
    public ClientConfigurationResource clientConfigurationResource(ClientConfigStorageService clientConfigStorageService, FlowRequestService flowRequestService, ClientConfigurationValidator clientConfigurationValidator, PermissionEnforcer permissionEnforcer, AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider, RedirectUriSuffixGenerator redirectUriSuffixGenerator, I18nResolver i18nResolver, ClientHttpsValidator clientHttpsValidator) {
        return new ClientConfigurationResource(clientConfigStorageService, flowRequestService, clientConfigurationValidator, permissionEnforcer, authorizationCodeFlowUrlsProvider, redirectUriSuffixGenerator, i18nResolver, clientHttpsValidator);
    }
}

