/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.InternalTypeAccessor
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.applinks.trusted.spring;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlassianPluginsComponentImports {
    @Bean
    public ApplicationLinkService applicationLinkService() {
        return OsgiServices.importOsgiService(ApplicationLinkService.class);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public AuthenticationConfigurationManager authenticationConfigurationManager() {
        return OsgiServices.importOsgiService(AuthenticationConfigurationManager.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public InternalHostApplication internalHostApplication() {
        return OsgiServices.importOsgiService(InternalHostApplication.class);
    }

    @Bean
    public InternalTypeAccessor internalTypeAccessor() {
        return OsgiServices.importOsgiService(InternalTypeAccessor.class);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return OsgiServices.importOsgiService(LocaleResolver.class);
    }

    @Bean
    public LoginUriProvider loginUriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public PluginAccessor pluginAccessor() {
        return OsgiServices.importOsgiService(PluginAccessor.class);
    }

    @Bean
    public RequestFactory<?> requestFactory() {
        return OsgiServices.importOsgiService(RequestFactory.class);
    }

    @Bean
    public TemplateRenderer templateRenderer() {
        return OsgiServices.importOsgiService(TemplateRenderer.class);
    }

    @Bean
    public TrustedApplicationsConfigurationManager trustedApplicationsConfigurationManager() {
        return OsgiServices.importOsgiService(TrustedApplicationsConfigurationManager.class);
    }

    @Bean
    public TrustedApplicationsManager trustedApplicationsManager() {
        return OsgiServices.importOsgiService(TrustedApplicationsManager.class);
    }

    @Bean
    public UserManager userManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public WebResourceManager webResourceManager() {
        return OsgiServices.importOsgiService(WebResourceManager.class);
    }

    @Bean
    public WebResourceUrlProvider webResourceUrlProvider() {
        return OsgiServices.importOsgiService(WebResourceUrlProvider.class);
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }

    @Bean
    public XsrfTokenAccessor xsrfTokenAccessor() {
        return OsgiServices.importOsgiService(XsrfTokenAccessor.class);
    }

    @Bean
    public XsrfTokenValidator xsrfTokenValidator() {
        return OsgiServices.importOsgiService(XsrfTokenValidator.class);
    }

    @Bean
    public HelpPathResolver helpPathResolver() {
        return OsgiServices.importOsgiService(HelpPathResolver.class);
    }
}

