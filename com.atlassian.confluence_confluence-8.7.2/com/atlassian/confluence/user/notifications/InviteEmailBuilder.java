/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 */
package com.atlassian.confluence.user.notifications;

import com.atlassian.confluence.event.events.user.SendUserInviteEvent;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.notifications.EmailContextBuilder;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import java.io.Serializable;

public class InviteEmailBuilder {
    private WikiStyleRenderer wikiStyleRenderer;
    private EmailContextBuilder emailContextBuilder;

    public InviteEmailBuilder(SettingsManager settingsManager, WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
        this.emailContextBuilder = new EmailContextBuilder(settingsManager);
    }

    public NotificationData buildFrom(SendUserInviteEvent event, String signupUrl) {
        NotificationData notificationData = new NotificationData(event.getSender(), true, null);
        String subjectMarkup = "$i18n.getText('email.user.invited.you', [$modifier.fullName, $siteTitle])";
        notificationData.setSubject(subjectMarkup);
        notificationData.setTemplateName("Confluence.Templates.Mail.Notifications.inviteUser.soy");
        String messageHtml = this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)new PageContext(), event.getMessage());
        notificationData.addToContext("messageHtml", (Serializable)((Object)messageHtml));
        notificationData.addToContext("signupURL", (Serializable)((Object)signupUrl));
        notificationData.addToContext("manageNotificationsOverride", Boolean.valueOf(true));
        notificationData.addAllToContext(this.emailContextBuilder.getSystemContext());
        return notificationData;
    }
}

