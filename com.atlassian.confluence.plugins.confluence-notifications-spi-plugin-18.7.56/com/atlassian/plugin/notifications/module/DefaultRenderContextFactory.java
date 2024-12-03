/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.notifications.api.event.EventContextBuilder
 *  com.atlassian.plugin.notifications.api.event.NotificationEvent
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.RenderContextFactoryTemplate
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.plugin.notifications.spi.salext.UserI18nResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.module;

import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.event.EventContextBuilder;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.RenderContextFactoryTemplate;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.notifications.spi.salext.UserI18nResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Map;

public class DefaultRenderContextFactory
extends RenderContextFactoryTemplate<NotificationEvent> {
    private final UserI18nResolver i18nResolver;

    public DefaultRenderContextFactory(UserI18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    protected Map<String, Object> createChecked(NotificationEvent event, ServerConfiguration serverConfiguration, Either<NotificationAddress, RoleRecipient> recipientData) {
        return this.createImpl(event, serverConfiguration, (RoleRecipient)(recipientData.isRight() ? (RoleRecipient)recipientData.right().get() : UserKeyRoleRecipient.UNKNOWN));
    }

    private Map<String, Object> createImpl(NotificationEvent event, ServerConfiguration serverConfiguration, RoleRecipient roleRecipient) {
        UserKey userKey = roleRecipient == UserKeyRoleRecipient.UNKNOWN ? null : roleRecipient.getUserKey();
        this.i18nResolver.setUser(userKey);
        return EventContextBuilder.buildContext((NotificationEvent)event, (I18nResolver)this.i18nResolver, (UserKey)userKey, (UserRole)roleRecipient.getRole(), (ServerConfiguration)serverConfiguration);
    }
}

