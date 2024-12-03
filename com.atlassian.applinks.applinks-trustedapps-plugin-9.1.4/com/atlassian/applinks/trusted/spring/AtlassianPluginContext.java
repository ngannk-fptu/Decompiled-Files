/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.AppLinkPluginUtil
 *  com.atlassian.applinks.core.DefaultAppLinkPluginUtil
 *  com.atlassian.applinks.core.RedirectController
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DefaultDocumentationLinker
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.applinks.ui.validators.CallbackParameterValidator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.FrameworkUtil
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.applinks.trusted.spring;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.DefaultAppLinkPluginUtil;
import com.atlassian.applinks.core.RedirectController;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DefaultDocumentationLinker;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.applinks.ui.validators.CallbackParameterValidator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlassianPluginContext {
    @Bean
    public AdminUIAuthenticator adminUIAuthenticator(UserManager userManager) {
        return new AdminUIAuthenticator(userManager);
    }

    @Bean
    public BundleContext bundleContext() {
        return FrameworkUtil.getBundle(AtlassianPluginContext.class).getBundleContext();
    }

    @Bean
    public CallbackParameterValidator callbackParameterValidator(MessageFactory messageFactory, InternalHostApplication internalHostApplication, ApplicationLinkService applicationLinkService) {
        return new CallbackParameterValidator(messageFactory, internalHostApplication, applicationLinkService);
    }

    @Bean
    public DefaultAppLinkPluginUtil defaultAppLinkPluginUtil(BundleContext bundleContext) {
        return new DefaultAppLinkPluginUtil(bundleContext);
    }

    @Bean
    public DefaultDocumentationLinker defaultDocumentationLinker(AppLinkPluginUtil appLinkPluginUtil, HelpPathResolver helpPathResolver, ApplicationProperties applicationProperties) {
        return new DefaultDocumentationLinker(appLinkPluginUtil, helpPathResolver, applicationProperties);
    }

    @Bean
    public MessageFactory messageFactory(I18nResolver i18nResolver) {
        return new MessageFactory(i18nResolver);
    }

    @Bean
    public RedirectController redirectController(CallbackParameterValidator callbackParameterValidator, TemplateRenderer templateRenderer, WebResourceManager webResourceManager) {
        return new RedirectController(callbackParameterValidator, templateRenderer, webResourceManager);
    }
}

