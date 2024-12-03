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
package com.atlassian.confluence.plugins.emailtopage.notifications;

import com.atlassian.confluence.core.MaybeNot;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.plugins.emailtopage.events.EmailThreadStagedPayload;
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

public class DefaultEmailThreadStagedRenderContextFactory
extends RenderContextProviderTemplate<EmailThreadStagedPayload> {
    private static final String CONFIRM_URL = "/email-to-page/confirm.action?hash=";
    private static final String REJECT_URL = "/email-to-page/reject.action?hash=";
    private final I18NBeanFactory i18NBeanFactory;
    private final SettingsManager settingsManager;

    public DefaultEmailThreadStagedRenderContextFactory(I18NBeanFactory i18NBeanFactory, SettingsManager settingsManager) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.settingsManager = settingsManager;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<EmailThreadStagedPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> addressData) {
        if (addressData.isEmpty()) {
            return MaybeNot.becauseOfNoResult(addressData);
        }
        if (((Either)addressData.get()).isRight()) {
            return MaybeNot.becauseOf((String)"Confirm notification must be sent to a direct medium, and not the user's preferences", (Object[])new Object[0]);
        }
        EmailThreadStagedPayload payload = (EmailThreadStagedPayload)notification.getPayload();
        HashMap context = Maps.newHashMapWithExpectedSize((int)6);
        String emailSubject = this.i18NBeanFactory.getI18NBean().getText("email.to.page.name.confirmation.emailsubject", (Object[])new String[]{this.settingsManager.getGlobalSettings().getSiteTitle()});
        String emailSubjectTitle = payload.isError() ? this.i18NBeanFactory.getI18NBean().getText("email.to.page.name.confirmation.emailsubjecttitle.nospace") : this.i18NBeanFactory.getI18NBean().getText("email.to.page.name.confirmation.emailsubjecttitle");
        context.put("subject", emailSubject);
        context.put("subjectTitle", emailSubjectTitle);
        context.put("error", payload.isError());
        context.put("pageTitle", payload.getPageTitle());
        context.put("confirmLink", this.settingsManager.getGlobalSettings().getBaseUrl() + CONFIRM_URL + payload.getHash());
        context.put("rejectLink", this.settingsManager.getGlobalSettings().getBaseUrl() + REJECT_URL + payload.getHash());
        return Option.option((Object)context);
    }
}

