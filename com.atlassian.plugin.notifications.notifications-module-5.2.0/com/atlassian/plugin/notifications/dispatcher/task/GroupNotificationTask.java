/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Lists
 */
package com.atlassian.plugin.notifications.dispatcher.task;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.DefaultNotificationAddress;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.plugin.notifications.api.medium.NotificationException;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.GroupRecipient;
import com.atlassian.plugin.notifications.api.queue.RecipientDescription;
import com.atlassian.plugin.notifications.api.queue.TaskStatus;
import com.atlassian.plugin.notifications.dispatcher.AbstractNotificationTask;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.NotificationQueueMonitor;
import com.atlassian.plugin.notifications.dispatcher.TaskComponents;
import com.atlassian.plugin.notifications.dispatcher.task.events.GroupNotificationSentEvent;
import com.atlassian.plugin.notifications.dispatcher.util.DeliveryStatus;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

public class GroupNotificationTask
extends AbstractNotificationTask {
    private final GroupRecipient recipient;

    public GroupNotificationTask(TaskComponents components, GroupRecipient recipient, NotificationEvent event, NotificationQueueMonitor monitor) {
        super(components, event, RecipientType.GROUP, monitor);
        this.recipient = recipient;
    }

    @Override
    public DeliveryStatus execute() {
        Message message;
        this.setState(TaskStatus.State.SENDING);
        ServerConfiguration config = this.components.getServerConfigurationManager().getServer(this.recipient.getServerId());
        if (config == null) {
            return DeliveryStatus.SUCCESS;
        }
        NotificationMedium notificationMedium = config.getNotificationMedium();
        if (notificationMedium == null) {
            this.components.getErrorRegistry().addError(config.getId(), this, new NotificationError("Failed to lookup notification medium"));
            return DeliveryStatus.ERROR;
        }
        Server server = this.components.getServerFactory().getServer(config);
        if (server == null) {
            this.components.getErrorRegistry().addError(config.getId(), this, new NotificationError("Failed to lookup server '" + config.getServerName() + "' of medium '" + notificationMedium + "'. Server not found."));
            return DeliveryStatus.ERROR;
        }
        Map<String, Object> context = this.components.getRenderContextFactory().create(this.event, server.getConfig());
        String groupAddressData = this.recipient.getParamValue();
        try {
            message = notificationMedium.renderMessage(RecipientType.GROUP, context, config);
            if (message == null) {
                NotificationError error = new NotificationError(String.format("Message failed to render for server '%s' on medium '%s' for group '%s'.", config.getServerName(), notificationMedium.getKey(), groupAddressData));
                this.components.getErrorRegistry().addError(config.getId(), this, error);
                return DeliveryStatus.ERROR;
            }
        }
        catch (Throwable throwable) {
            NotificationError error = new NotificationError(String.format("Message failed to render for server '%s' on medium '%s' for group '%s'.", config.getServerName(), notificationMedium.getKey(), groupAddressData), throwable);
            this.components.getErrorRegistry().addError(config.getId(), this, error);
            return DeliveryStatus.ERROR;
        }
        try {
            server.sendGroupNotification(new DefaultNotificationAddress((Option<String>)Option.option((Object)notificationMedium.getKey()), groupAddressData), message);
            this.components.getErrorRegistry().logSuccess(config.getId());
            GroupNotificationSentEvent analyticsEvent = new GroupNotificationSentEvent(this.event.getKey(), notificationMedium.getKey());
            this.components.getAnalyticsPublisher().publishEvent(analyticsEvent, (Option<UserKey>)Option.none(UserKey.class));
        }
        catch (NotificationException e) {
            this.components.getErrorRegistry().addError(config.getId(), this, new NotificationError("Error sending to group '" + groupAddressData + "' on server '" + config.getServerName() + "'", e));
            return DeliveryStatus.ERROR;
        }
        return DeliveryStatus.SUCCESS;
    }

    @Override
    public List<RecipientDescription> getRecipientDescriptions(I18nResolver i18n) {
        return Lists.newArrayList((Object[])new RecipientDescription[]{new RecipientDescription(false, this.recipient.getName() + " '" + this.recipient.getParamDisplay() + "'")});
    }
}

