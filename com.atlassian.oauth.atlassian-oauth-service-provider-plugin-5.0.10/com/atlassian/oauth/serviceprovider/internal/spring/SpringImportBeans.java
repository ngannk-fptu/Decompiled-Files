/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationController
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.auth.OAuthRequestVerifierFactory
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth.serviceprovider.internal.spring;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationController;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.auth.OAuthRequestVerifierFactory;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringImportBeans {
    @Bean
    public ApplicationLinkService applicationLinkService() {
        return (ApplicationLinkService)OsgiServices.importOsgiService(ApplicationLinkService.class);
    }

    @Bean
    public ServiceProviderTokenStore delegateTokenStore() {
        return (ServiceProviderTokenStore)OsgiServices.importOsgiService(ServiceProviderTokenStore.class);
    }

    @Bean
    public ServiceProviderConsumerStore serviceProviderConsumerStore() {
        return (ServiceProviderConsumerStore)OsgiServices.importOsgiService(ServiceProviderConsumerStore.class);
    }

    @Bean
    public OAuthRequestVerifierFactory oAuthRequestVerifierFactory() {
        return (OAuthRequestVerifierFactory)OsgiServices.importOsgiService(OAuthRequestVerifierFactory.class);
    }

    @Bean
    public XsrfTokenValidator xsrfTokenValidator() {
        return (XsrfTokenValidator)OsgiServices.importOsgiService(XsrfTokenValidator.class);
    }

    @Bean
    public XsrfTokenAccessor xsrfTokenAccessor() {
        return (XsrfTokenAccessor)OsgiServices.importOsgiService(XsrfTokenAccessor.class);
    }

    @Bean
    public AuthenticationController authenticationController() {
        return (AuthenticationController)OsgiServices.importOsgiService(AuthenticationController.class);
    }

    @Bean
    public AuthenticationListener authenticationListener() {
        return (AuthenticationListener)OsgiServices.importOsgiService(AuthenticationListener.class);
    }

    @Bean
    public LoginUriProvider loginUriProvider() {
        return (LoginUriProvider)OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public UserManager userManager() {
        return (UserManager)OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return (LocaleResolver)OsgiServices.importOsgiService(LocaleResolver.class);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return (I18nResolver)OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        return (TransactionTemplate)OsgiServices.importOsgiService(TransactionTemplate.class);
    }

    @Bean
    public RequestFactory requestFactory() {
        return (RequestFactory)OsgiServices.importOsgiService(RequestFactory.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return (SchedulerService)OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public TemplateRenderer templateRenderer() {
        return (TemplateRenderer)OsgiServices.importOsgiService(TemplateRenderer.class);
    }

    @Bean
    @Conditional(value={JiraOnly.class})
    public HttpContext httpContext() {
        return (HttpContext)OsgiServices.importOsgiService(HttpContext.class);
    }
}

