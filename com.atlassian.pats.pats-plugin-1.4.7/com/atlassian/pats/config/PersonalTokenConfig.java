/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.TransactionalAnnotationProcessor
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.ComponentScan
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.pats.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.TransactionalAnnotationProcessor;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.data.activeobjects.repository.config.EnableActiveObjectsRepositories;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.pats.api.TokenAuthenticationService;
import com.atlassian.pats.api.TokenGeneratorService;
import com.atlassian.pats.api.TokenMailSenderService;
import com.atlassian.pats.api.TokenService;
import com.atlassian.pats.api.TokenValidator;
import com.atlassian.pats.checker.ProductUserProvider;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.db.PersonalTokenConfigEnricher;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.events.TokenEventPublisher;
import com.atlassian.pats.events.audit.AuditEventListener;
import com.atlassian.pats.events.audit.AuditLogHandler;
import com.atlassian.pats.jobs.AnalyticsJob;
import com.atlassian.pats.jobs.DeletedUserPruningJob;
import com.atlassian.pats.jobs.ExpiredTokenPruningJob;
import com.atlassian.pats.jobs.ExpiryDateTokenCheckEventJob;
import com.atlassian.pats.jobs.LastAccessedTimeBatcherJob;
import com.atlassian.pats.notifications.TokenEventsListener;
import com.atlassian.pats.notifications.mail.DefaultTokenMailSenderService;
import com.atlassian.pats.notifications.mail.MailRenderer;
import com.atlassian.pats.notifications.mail.MailStyleLoader;
import com.atlassian.pats.notifications.mail.services.ProductMailService;
import com.atlassian.pats.rest.PermissionChecker;
import com.atlassian.pats.rest.RestValidator;
import com.atlassian.pats.service.CachingTokenValidator;
import com.atlassian.pats.service.DefaultTokenAuthenticationService;
import com.atlassian.pats.service.DefaultTokenGeneratorService;
import com.atlassian.pats.service.DefaultTokenService;
import com.atlassian.pats.service.DefaultTokenValidator;
import com.atlassian.pats.user.DeletedUserPruningService;
import com.atlassian.pats.utils.LicenseChecker;
import com.atlassian.pats.utils.ProductHelper;
import com.atlassian.pats.web.filter.LastAccessedTimeBatcher;
import com.atlassian.pats.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.pats.web.loopsprevention.SeraphRedirectsLoopPreventer;
import com.atlassian.pats.web.loopsprevention.SeraphRedirectsLoopPreventerCondition;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.pocketknife.api.querydsl.configuration.ConfigurationEnrichment;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.security.password.DefaultPasswordEncoder;
import com.atlassian.security.password.PasswordEncoder;
import com.atlassian.security.random.DefaultSecureRandomService;
import com.atlassian.security.random.SecureRandomService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.time.Clock;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.config.BootstrapMode;

@Configuration
@ComponentScan(basePackages={"com.atlassian.pocketknife.internal.querydsl"})
@EnableActiveObjectsRepositories(basePackageClasses={TokenRepository.class}, bootstrapMode=BootstrapMode.LAZY)
public class PersonalTokenConfig {
    public static final String AUTH_CACHE_NAME = "authResultCache";

    @Bean
    public PersonalTokenConfigEnricher personalTokenConfigEnricher(ConfigurationEnrichment configurationEnrichment) {
        return new PersonalTokenConfigEnricher(configurationEnrichment);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return DefaultPasswordEncoder.getDefaultInstance();
    }

    @Bean
    public SecureRandomService secureRandomService() {
        return DefaultSecureRandomService.getInstance();
    }

    @Bean
    public TokenGeneratorService tokenGeneratorService(PasswordEncoder passwordEncoder, SecureRandomService secureRandomService) {
        return new DefaultTokenGeneratorService(passwordEncoder, secureRandomService);
    }

    @Bean
    public TokenService tokenService(TokenRepository tokenRepository, TokenGeneratorService tokenGeneratorService, UserManager userManager, CrowdService crowdService, Clock utcClock, TokenEventPublisher tokenEventPublisher) {
        return new DefaultTokenService(tokenRepository, tokenGeneratorService, userManager, crowdService, utcClock, tokenEventPublisher);
    }

    @Bean
    public CrowdService crowdService() {
        return OsgiServices.importOsgiService(CrowdService.class);
    }

    @Bean
    public WebResourceUrlProvider webResourceUrlProvider() {
        return OsgiServices.importOsgiService(WebResourceUrlProvider.class);
    }

    @Bean
    public ActiveObjects activeObjects() {
        return OsgiServices.importOsgiService(ActiveObjects.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public TransactionalExecutorFactory transactionalExecutorFactory() {
        return OsgiServices.importOsgiService(TransactionalExecutorFactory.class);
    }

    @Bean
    public TransactionalAnnotationProcessor transactionalAnnotationProcessor(ActiveObjects activeObjects) {
        return new TransactionalAnnotationProcessor(activeObjects);
    }

    @Bean
    public UserManager userManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public PermissionEnforcer permissionEnforcer() {
        return OsgiServices.importOsgiService(PermissionEnforcer.class);
    }

    @Bean
    public RestValidator restValidator(I18nResolver i18nResolver, TokenRepository tokenRepository) {
        return new RestValidator(i18nResolver, tokenRepository);
    }

    @Bean
    public SoyTemplateRenderer soyTemplateRenderer() {
        return OsgiServices.importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    public AuthenticationListener authenticationListener() {
        return OsgiServices.importOsgiService(AuthenticationListener.class);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean(destroyMethod="")
    public CacheManager cacheManager() {
        return OsgiServices.importOsgiService(CacheManager.class);
    }

    @Bean
    public Clock utcClock() {
        return Clock.systemUTC();
    }

    @Bean
    public SchedulerService schedulerService() {
        return OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public ExpiredTokenPruningJob expiredTokensPruningJob(SchedulerService schedulerService, Clock utcClock, TokenEventPublisher tokenEventPublisher, TokenRepository tokenRepository) {
        return new ExpiredTokenPruningJob(schedulerService, utcClock, tokenEventPublisher, tokenRepository);
    }

    @Bean
    public DeletedUserPruningJob deletedUserPruningJob(SchedulerService schedulerService, TokenService tokenService, TokenRepository tokenRepository, ProductUserProvider productUserProvider) {
        return new DeletedUserPruningJob(schedulerService, tokenService, tokenRepository, productUserProvider);
    }

    @Bean
    public AnalyticsJob analyticsJob(SchedulerService schedulerService, TokenRepository tokenRepository, TokenEventPublisher tokenEventPublisher) {
        return new AnalyticsJob(schedulerService, tokenRepository, tokenEventPublisher);
    }

    @Bean
    public TokenEventPublisher analyticsEventPublisher(EventPublisher eventPublisher, Clock utcClock) {
        return new TokenEventPublisher(eventPublisher, utcClock);
    }

    @Bean
    public LastAccessedTimeBatcher lastAccessedTimeBatcher() {
        return new LastAccessedTimeBatcher();
    }

    @Bean
    public LastAccessedTimeBatcherJob lastAccessedTimeBatcherJob(SchedulerService schedulerService, TokenRepository tokenRepository, LastAccessedTimeBatcher lastAccessedTimeBatcher) {
        return new LastAccessedTimeBatcherJob(schedulerService, lastAccessedTimeBatcher, tokenRepository);
    }

    @Bean
    public TokenMailSenderService tokenMailSenderService(ProductMailService productMailService, MailRenderer mailRenderer) {
        return new DefaultTokenMailSenderService(productMailService, mailRenderer);
    }

    @Bean
    public TokenEventsListener tokenEventsListener(EventPublisher eventPublisher, DefaultTokenMailSenderService tokenMailSenderService) {
        return new TokenEventsListener(eventPublisher, tokenMailSenderService);
    }

    @Bean
    public MailRenderer mailRenderer(I18nResolver i18nResolver, SoyTemplateRenderer soyTemplateRenderer, UserManager userManager, MailStyleLoader mailStyleLoader, ProductHelper productHelper) {
        return new MailRenderer(i18nResolver, soyTemplateRenderer, userManager, mailStyleLoader, productHelper);
    }

    @Bean
    public MailStyleLoader mailStyleLoader() {
        return new MailStyleLoader();
    }

    @Bean
    public TokenValidator tokenValidator(PasswordEncoder passwordEncoder, CacheManager cacheManager) {
        DefaultTokenValidator defaultTokenValidator = new DefaultTokenValidator(passwordEncoder);
        return 0 != SystemProperty.AUTH_CACHE_EXPIRY_MINS.getValue() ? new CachingTokenValidator(this.authenticationCache(cacheManager), defaultTokenValidator) : defaultTokenValidator;
    }

    private Cache<Long, TokenAuthenticationService.AuthenticationResult> authenticationCache(CacheManager cacheManager) {
        CacheSettings cacheSettings = new CacheSettingsBuilder().local().expireAfterWrite((long)SystemProperty.AUTH_CACHE_EXPIRY_MINS.getValue().intValue(), TimeUnit.MINUTES).maxEntries(SystemProperty.AUTH_CACHE_MAX_ITEMS.getValue().intValue()).statisticsEnabled().build();
        return cacheManager.getCache(AUTH_CACHE_NAME, null, cacheSettings);
    }

    @Bean
    public TokenAuthenticationService tokenAuthenticationService(TokenRepository tokenRepository, Clock utcClock, TokenValidator tokenValidator, I18nResolver i18nResolver) {
        return new DefaultTokenAuthenticationService(tokenValidator, tokenRepository, utcClock, i18nResolver);
    }

    @Bean
    public LoginUriProvider loginUriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }

    @Bean
    @Conditional(value={SeraphRedirectsLoopPreventerCondition.class})
    public RedirectsLoopPreventer seraphRedirectsLoopPreventer() {
        return new SeraphRedirectsLoopPreventer();
    }

    @Bean
    public ExpiryDateTokenCheckEventJob expiredTokenEventSenderJob(SchedulerService schedulerService, Clock utcClock, TokenEventPublisher tokenEventPublisher, TokenRepository tokenRepository) {
        return new ExpiryDateTokenCheckEventJob(schedulerService, utcClock, tokenEventPublisher, tokenRepository);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return OsgiServices.importOsgiService(LocaleResolver.class);
    }

    @Bean
    public DeletedUserPruningService deletedUserPruningService(EventPublisher eventPublisher, TokenService tokenService, ProductUserProvider productUserProvider) {
        return new DeletedUserPruningService(eventPublisher, tokenService, productUserProvider);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public TimeZoneManager timeZoneManager() {
        return OsgiServices.importOsgiService(TimeZoneManager.class);
    }

    @Bean
    public AuditEventListener auditEventListener(EventPublisher eventPublisher, AuditLogHandler auditLogHandler) {
        return new AuditEventListener(eventPublisher, auditLogHandler);
    }

    @Bean
    public LicenseHandler licenseHandler() {
        return OsgiServices.importOsgiService(LicenseHandler.class);
    }

    @Bean
    public LicenseChecker licenseChecker(LicenseHandler licenseHandler) {
        return new LicenseChecker(licenseHandler);
    }

    @Bean
    public PermissionChecker permissionChecker(PermissionEnforcer permissionEnforcer, LicenseChecker licenseChecker, I18nResolver i18nResolver) {
        return new PermissionChecker(permissionEnforcer, licenseChecker, i18nResolver);
    }
}

