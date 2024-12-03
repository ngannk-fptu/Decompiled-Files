/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.Clock
 *  com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.oauth.serviceprovider.SystemClock
 *  com.atlassian.oauth.serviceprovider.TokenPropertiesFactory
 *  com.atlassian.oauth.shared.servlet.HelpLinkResolver
 *  com.atlassian.oauth.shared.servlet.Unescaper
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.security.random.DefaultSecureRandomService
 *  com.atlassian.security.random.SecureRandomService
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  net.oauth.OAuthValidator
 *  org.apache.commons.text.StringEscapeUtils
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.DependsOn
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.oauth.serviceprovider.internal.spring;

import com.atlassian.oauth.serviceprovider.Clock;
import com.atlassian.oauth.serviceprovider.ConsumerInformationRenderer;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.oauth.serviceprovider.SystemClock;
import com.atlassian.oauth.serviceprovider.TokenPropertiesFactory;
import com.atlassian.oauth.serviceprovider.internal.AggregateTokenPropertiesFactory;
import com.atlassian.oauth.serviceprovider.internal.InMemoryCacheNonceChecker;
import com.atlassian.oauth.serviceprovider.internal.NonceChecker;
import com.atlassian.oauth.serviceprovider.internal.OAuthConverter;
import com.atlassian.oauth.serviceprovider.internal.OAuthValidatorImpl;
import com.atlassian.oauth.serviceprovider.internal.Randomizer;
import com.atlassian.oauth.serviceprovider.internal.RandomizerImpl;
import com.atlassian.oauth.serviceprovider.internal.ServiceProviderFactory;
import com.atlassian.oauth.serviceprovider.internal.ServiceProviderFactoryImpl;
import com.atlassian.oauth.serviceprovider.internal.TokenFactory;
import com.atlassian.oauth.serviceprovider.internal.TokenFactoryImpl;
import com.atlassian.oauth.serviceprovider.internal.oauth2.OAuth2OsgiServiceFactory;
import com.atlassian.oauth.serviceprovider.internal.servlet.TokenLoader;
import com.atlassian.oauth.serviceprovider.internal.servlet.TokenLoaderImpl;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRenderer;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.AuthorizationRendererImpl;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.BasicConsumerInformationRenderer;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.LoginRedirector;
import com.atlassian.oauth.serviceprovider.internal.servlet.authorize.LoginRedirectorImpl;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensRevoke;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletContext;
import com.atlassian.oauth.serviceprovider.internal.servlet.user.AccessTokensServletValidation;
import com.atlassian.oauth.serviceprovider.internal.spring.SpringExportBeans;
import com.atlassian.oauth.serviceprovider.internal.spring.SpringImportBeans;
import com.atlassian.oauth.shared.servlet.HelpLinkResolver;
import com.atlassian.oauth.shared.servlet.Unescaper;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.security.random.DefaultSecureRandomService;
import com.atlassian.security.random.SecureRandomService;
import com.atlassian.templaterenderer.TemplateRenderer;
import net.oauth.OAuthValidator;
import org.apache.commons.text.StringEscapeUtils;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value={SpringImportBeans.class, SpringExportBeans.class})
public class SpringBeans {
    @Bean
    public AuthorizationRenderer authorizationRenderer(ApplicationProperties applicationProperties, TemplateRenderer templateRenderer, @Qualifier(value="consumerInfoRenderers") Iterable<ConsumerInformationRenderer> consumerInfoRenderers, BasicConsumerInformationRenderer basicConsumerInformationRenderer, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        return new AuthorizationRendererImpl(applicationProperties, templateRenderer, consumerInfoRenderers, basicConsumerInformationRenderer, xsrfTokenAccessor, xsrfTokenValidator);
    }

    @Bean
    public TokenPropertiesFactory aggregateTokenPropertiesFactory(@Qualifier(value="tokenPropertyFactories") Iterable<TokenPropertiesFactory> tokenPropertyFactories) {
        return new AggregateTokenPropertiesFactory(tokenPropertyFactories);
    }

    @Bean
    public HelpLinkResolver helpLinkResolver() {
        return new HelpLinkResolver();
    }

    @Bean
    public Unescaper unescaper() {
        return new Unescaper();
    }

    @Bean
    public StringEscapeUtils stringEscapeUtil() {
        return new StringEscapeUtils();
    }

    @Bean
    @DependsOn(value={"secureRandomService"})
    public Randomizer randomizer(SecureRandomService secureRandomService) {
        return new RandomizerImpl(secureRandomService);
    }

    @Bean
    public TokenFactory tokenFactory(TokenPropertiesFactory propertiesFactory, Randomizer randomizer) {
        return new TokenFactoryImpl(propertiesFactory, randomizer);
    }

    @Bean
    public Clock clock() {
        return new SystemClock();
    }

    @Bean
    public ServiceProviderFactory serviceProviderFactory(ApplicationProperties applicationProperties) {
        return new ServiceProviderFactoryImpl(applicationProperties);
    }

    @Bean
    public OAuthValidator oAuthValidator(NonceChecker nonceChecker) {
        return new OAuthValidatorImpl(nonceChecker);
    }

    @Bean
    public NonceChecker localCacheNonceChecker() {
        return new InMemoryCacheNonceChecker();
    }

    @Bean
    public OAuthConverter oAuthConverter(ServiceProviderFactory serviceProviderFactory) {
        return new OAuthConverter(serviceProviderFactory);
    }

    @Bean
    public BasicConsumerInformationRenderer basicConsumerInformationRenderer(ApplicationProperties applicationProperties, TemplateRenderer templateRenderer, UserManager userManager) {
        return new BasicConsumerInformationRenderer(applicationProperties, templateRenderer, userManager);
    }

    @Bean
    public TokenLoader tokenLoader(@Qualifier(value="tokenStore") ServiceProviderTokenStore serviceProviderTokenStore, Clock clock) {
        return new TokenLoaderImpl(serviceProviderTokenStore, clock);
    }

    @Bean
    public LoginRedirector loginRedirector(UserManager userManager, LoginUriProvider loginUriProvider) {
        return new LoginRedirectorImpl(userManager, loginUriProvider);
    }

    @Bean
    public AccessTokensServletValidation accessTokenServletValidation(UserManager userManager) {
        return new AccessTokensServletValidation(userManager);
    }

    @Bean
    public OAuth2OsgiServiceFactory oAuth2ProviderServiceFactory(BundleContext bundleContext) {
        return new OAuth2OsgiServiceFactory(bundleContext);
    }

    @Bean
    public AccessTokensServletContext accessTokenServletContext(LocaleResolver localeResolver, ApplicationProperties applicationProperties, @Qualifier(value="tokenStore") ServiceProviderTokenStore store, OAuth2OsgiServiceFactory oAuth2OsgiServiceFactory) {
        return new AccessTokensServletContext(localeResolver, applicationProperties, store, oAuth2OsgiServiceFactory);
    }

    @Bean
    public AccessTokensRevoke accessTokenRevoke(UserManager userManager, @Qualifier(value="tokenStore") ServiceProviderTokenStore store) {
        return new AccessTokensRevoke(userManager, store);
    }

    @Bean
    public SecureRandomService secureRandomService() {
        return DefaultSecureRandomService.getInstance();
    }
}

