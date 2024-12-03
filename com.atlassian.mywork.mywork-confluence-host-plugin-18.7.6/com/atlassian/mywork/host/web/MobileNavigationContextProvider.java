/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.mywork.service.NotificationService
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.mywork.host.web;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.mywork.host.service.LocalRegistrationService;
import com.atlassian.mywork.service.NotificationService;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class MobileNavigationContextProvider
implements ContextProvider {
    private final ContextPathHolder contextPathHolder;
    private final NotificationService notificationService;
    private final UserManager userManager;
    private final I18nResolver i18nResolver;
    private final LocaleResolver localeManager;
    private final LocalRegistrationService registrationService;

    public MobileNavigationContextProvider(ContextPathHolder contextPathHolder, NotificationService notificationService, UserManager userManager, I18nResolver i18nResolver, LocaleResolver localeManager, LocalRegistrationService registrationService) {
        this.contextPathHolder = contextPathHolder;
        this.notificationService = notificationService;
        this.userManager = userManager;
        this.i18nResolver = i18nResolver;
        this.localeManager = localeManager;
        this.registrationService = registrationService;
    }

    public void init(Map<String, String> params) {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        String username = this.userManager.getRemoteUsername();
        int unseenNotificationCount = 0;
        if (username != null) {
            unseenNotificationCount = this.notificationService.getCount(username);
        }
        return ImmutableMap.of((Object)"contextPath", (Object)this.contextPathHolder.getContextPath(), (Object)"unseenNotificationCount", (Object)unseenNotificationCount, (Object)"i18n", (Object)this.i18nResolver, (Object)"configurationVersion", (Object)this.registrationService.getCacheValue(this.localeManager.getLocale()), (Object)"requestTime", (Object)System.currentTimeMillis());
    }
}

