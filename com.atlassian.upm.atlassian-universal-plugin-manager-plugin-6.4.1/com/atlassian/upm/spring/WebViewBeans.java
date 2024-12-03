/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.osgi.framework.BundleContext
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.upm.transformers.notification.NotificationsPageDataProvider;
import com.atlassian.upm.transformers.template.UnderscoreTemplateRenderer;
import com.atlassian.upm.transformers.webresource.UrlReadingWebResourceUrlBuilder;
import org.osgi.framework.BundleContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebViewBeans {
    @Bean
    public NotificationsPageDataProvider notificationsPageDataProvider(ApplicationProperties applicationProperties) {
        return new NotificationsPageDataProvider(applicationProperties);
    }

    @Bean
    public UnderscoreTemplateRenderer underscoreTemplateRenderer(I18nResolver i18nResolver, TemplateRenderer templateRenderer) {
        return new UnderscoreTemplateRenderer(i18nResolver, templateRenderer);
    }

    @Bean
    public UrlReadingWebResourceUrlBuilder urlReadingWebResourceUrlBuilder(BundleContext bundleContext) {
        return new UrlReadingWebResourceUrlBuilder(bundleContext);
    }
}

