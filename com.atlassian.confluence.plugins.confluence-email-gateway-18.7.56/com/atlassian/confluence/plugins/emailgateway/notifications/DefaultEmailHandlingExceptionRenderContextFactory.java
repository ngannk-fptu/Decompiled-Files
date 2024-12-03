/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.MaybeNot
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.emailgateway.notifications;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.plugins.emailgateway.events.EmailHandlingExceptionPayload;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class DefaultEmailHandlingExceptionRenderContextFactory
extends RenderContextProviderTemplate<EmailHandlingExceptionPayload> {
    private final I18NBeanFactory i18NBeanFactory;
    private final SettingsManager settingsManager;

    public DefaultEmailHandlingExceptionRenderContextFactory(I18NBeanFactory i18NBeanFactory, SettingsManager settingsManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.settingsManager = settingsManager;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<EmailHandlingExceptionPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> addressData) {
        String emailSubjectTitle;
        String emailSubject;
        if (addressData.isEmpty()) {
            return MaybeNot.becauseOfNoResult(addressData);
        }
        if (((Either)addressData.get()).isRight()) {
            return MaybeNot.becauseOf((String)"Confirm notification must be sent to a direct medium, and not the user's preferences", (Object[])new Object[0]);
        }
        EmailHandlingExceptionPayload payload = (EmailHandlingExceptionPayload)notification.getPayload();
        HashMap context = Maps.newHashMapWithExpectedSize((int)5);
        if (payload.isCreatePageError()) {
            emailSubject = this.i18NBeanFactory.getI18NBean().getText("email.gateway.no.matching.user.page.emailsubject", (Object[])new String[]{this.settingsManager.getGlobalSettings().getSiteTitle()});
            emailSubjectTitle = this.i18NBeanFactory.getI18NBean().getText(payload.isAttachmentError() ? "email.gateway.big.attachment.page.emailsubjecttitle" : "email.gateway.no.matching.user.page.emailsubjecttitle");
        } else {
            emailSubject = this.i18NBeanFactory.getI18NBean().getText("email.gateway.no.matching.user.comment.emailsubject", (Object[])new String[]{this.settingsManager.getGlobalSettings().getSiteTitle()});
            emailSubjectTitle = this.i18NBeanFactory.getI18NBean().getText(payload.isAttachmentError() ? "email.gateway.big.attachment.comment.emailsubjecttitle" : "email.gateway.no.matching.user.comment.emailsubjecttitle");
        }
        context.put("subject", emailSubject);
        context.put("subjectTitle", emailSubjectTitle);
        context.put("emailSubject", payload.getEmailSubject());
        context.put("createPageError", payload.isCreatePageError());
        context.put("attachmentError", payload.isAttachmentError());
        return Option.option((Object)context);
    }
}

