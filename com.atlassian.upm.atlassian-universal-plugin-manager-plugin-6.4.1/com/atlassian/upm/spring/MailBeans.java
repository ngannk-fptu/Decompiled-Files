/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UserSettingsStore;
import com.atlassian.upm.mail.MailRenderer;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmMailSenderService;
import com.atlassian.upm.mail.impl.MailRendererImpl;
import com.atlassian.upm.mail.impl.UpmMailSenderServiceImpl;
import com.atlassian.upm.rest.UpmUriBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailBeans {
    @Bean
    public MailRenderer mailRenderer(TemplateRenderer renderer, I18nResolver i18nResolver) {
        return new MailRendererImpl(renderer, i18nResolver);
    }

    @Bean
    public UpmMailSenderService upmMailSenderService(ProductMailService mailService, MailRenderer renderer, UserManager userManager, SysPersisted sysPersisted, UserSettingsStore userSettingsStore, UpmUriBuilder uriBuilder, ApplicationProperties applicationProperties, I18nResolver i18nResolver) {
        return new UpmMailSenderServiceImpl(mailService, renderer, userManager, sysPersisted, userSettingsStore, uriBuilder, applicationProperties, i18nResolver);
    }
}

