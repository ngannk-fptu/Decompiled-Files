/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.UserI18NBeanFactory
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.plugins.mobile.contextprovider;

import com.atlassian.confluence.plugins.mobile.notification.PushNotificationStatus;
import com.atlassian.confluence.plugins.mobile.service.PushNotificationService;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;

public final class MobileConfigurationContextProvider
implements ContextProvider {
    private final UserI18NBeanFactory i18NBeanFactory;
    private final PushNotificationService pushNotificationService;
    private final PluginAccessor pluginAccessor;

    public MobileConfigurationContextProvider(UserI18NBeanFactory i18NBeanFactory, PushNotificationService pushNotificationService, PluginAccessor pluginAccessor) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.pushNotificationService = pushNotificationService;
        this.pluginAccessor = pluginAccessor;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        context.put("i18n", this.i18NBeanFactory.getI18NBean());
        boolean isWorkBoxPluginDisabled = !this.pluginAccessor.isPluginEnabled("com.atlassian.mywork.mywork-confluence-host-plugin");
        context.put("isWorkBoxPluginDisabled", isWorkBoxPluginDisabled);
        context.put("isPushNotificationEnabled", !isWorkBoxPluginDisabled && this.pushNotificationService.getStatus() == PushNotificationStatus.ENABLED);
        return context;
    }
}

