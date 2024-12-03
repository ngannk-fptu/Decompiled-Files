/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.mail.notification.Notification$WatchType
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.NotificationUserService
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.notifications.content.WatchTypeUtil
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.user.User
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.efi.emails.notifications;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.efi.emails.notifications.OnboardingPayload;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationUserService;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.content.WatchTypeUtil;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.user.User;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class OnboardingRenderContextFactory
extends RenderContextProviderTemplate<OnboardingPayload> {
    private static final String EMAIL_TEMPLATE_RESOURCE_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-onboarding:onboarding-email-soy-templates";
    private final NotificationUserService notificationUserService;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final SettingsManager settingsManager;

    public OnboardingRenderContextFactory(@ComponentImport NotificationUserService notificationUserService, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport SettingsManager settingsManager) {
        this.notificationUserService = notificationUserService;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.settingsManager = settingsManager;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<OnboardingPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        if (roleRecipient.isEmpty()) {
            return MaybeNot.becauseOfNoResult(roleRecipient);
        }
        RoleRecipient recipient = (RoleRecipient)((Either)roleRecipient.get()).right().get();
        User recipientUser = this.notificationUserService.findUserForKey(recipient.getUserKey());
        User actionUser = this.notificationUserService.findUserForKey(recipientUser, notification.getOriginator());
        NotificationContext notificationContext = new NotificationContext();
        notificationContext.setActor(actionUser);
        notificationContext.setRecipient(recipientUser);
        Maybe watchType = WatchTypeUtil.computeWatchTypeFrom((UserRole)recipient.getRole());
        if (watchType.isDefined()) {
            notificationContext.setWatchType((Notification.WatchType)watchType.get());
        }
        HashMap enhancedContext = Maps.newHashMap();
        enhancedContext.put("baseUrl", this.settingsManager.getGlobalSettings().getBaseUrl());
        enhancedContext.put("createSpaceImgUrl", this.webResourceUrlProvider.getStaticPluginResourceUrl(EMAIL_TEMPLATE_RESOURCE_MODULE_KEY, "notifications/images/create-space.png", UrlMode.ABSOLUTE));
        enhancedContext.put("teamImgUrl", this.webResourceUrlProvider.getStaticPluginResourceUrl(EMAIL_TEMPLATE_RESOURCE_MODULE_KEY, "notifications/images/team.png", UrlMode.ABSOLUTE));
        enhancedContext.put("confluenceImgUrl", this.webResourceUrlProvider.getStaticPluginResourceUrl(EMAIL_TEMPLATE_RESOURCE_MODULE_KEY, "notifications/images/confluence.png", UrlMode.ABSOLUTE));
        enhancedContext.put("atlassianImgUrl", this.webResourceUrlProvider.getStaticPluginResourceUrl(EMAIL_TEMPLATE_RESOURCE_MODULE_KEY, "notifications/images/atlassian.png", UrlMode.ABSOLUTE));
        enhancedContext.put("versionNumber", GeneralUtil.getVersionNumber());
        notificationContext.putAll((Map)enhancedContext);
        return Option.option((Object)notificationContext.getMap());
    }
}

