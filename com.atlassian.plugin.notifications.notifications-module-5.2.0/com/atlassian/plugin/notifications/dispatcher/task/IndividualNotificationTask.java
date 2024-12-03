/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package com.atlassian.plugin.notifications.dispatcher.task;

import com.atlassian.fugue.Either;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.NotificationException;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.api.medium.Server;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.queue.RecipientDescription;
import com.atlassian.plugin.notifications.api.queue.TaskStatus;
import com.atlassian.plugin.notifications.dispatcher.AbstractNotificationTask;
import com.atlassian.plugin.notifications.dispatcher.IndividualRecipientPreferences;
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.NotificationQueueMonitor;
import com.atlassian.plugin.notifications.dispatcher.TaskComponents;
import com.atlassian.plugin.notifications.dispatcher.task.SendResult;
import com.atlassian.plugin.notifications.dispatcher.task.Sender;
import com.atlassian.plugin.notifications.dispatcher.task.events.IndividualNotificationSentEvent;
import com.atlassian.plugin.notifications.dispatcher.util.DeliveryStatus;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IndividualNotificationTask
extends AbstractNotificationTask {
    private final Iterable<RoleRecipient> recipients;
    private final IndividualRecipientPreferences recipientPreferences;
    private final Map<Integer, Set<UserKey>> usersNotifiedPerServer = Maps.newHashMap();

    public IndividualNotificationTask(TaskComponents components, Iterable<RoleRecipient> roleRecipients, NotificationEvent event, IndividualRecipientPreferences recipientPreferences, NotificationQueueMonitor monitor) {
        super(components, event, RecipientType.INDIVIDUAL, monitor);
        this.recipients = Lists.newArrayList(roleRecipients);
        this.recipientPreferences = recipientPreferences;
    }

    @Override
    public DeliveryStatus execute() {
        this.setState(TaskStatus.State.SENDING);
        Iterator<RoleRecipient> iterator = this.recipients.iterator();
        while (iterator.hasNext()) {
            RoleRecipient recipient = iterator.next();
            SendResult sendResult = new UserKeySender(recipient, this.usersNotifiedPerServer).send();
            if (!sendResult.isSuccessfulSendForRecipient() && sendResult.isAnyEnabledServers()) continue;
            iterator.remove();
        }
        if (Iterables.size(this.recipients) > 0) {
            return DeliveryStatus.ERROR;
        }
        return DeliveryStatus.SUCCESS;
    }

    @Override
    public List<RecipientDescription> getRecipientDescriptions(I18nResolver i18n) {
        ArrayList<RecipientDescription> list = new ArrayList<RecipientDescription>();
        for (RoleRecipient rec : this.recipients) {
            list.add(new RecipientDescription(true, i18n.getText("notifications.roles." + rec.getRole().getID())));
        }
        return list;
    }

    private class UserKeySender
    implements Sender,
    SendResult {
        private boolean successfulSendForRecipient = true;
        private boolean anyEnabledServers = false;
        private final Map<Integer, Set<UserKey>> usersNotifiedPerServer;
        private final RoleRecipient recipient;
        private final UserKey userKey;

        public UserKeySender(RoleRecipient recipient, Map<Integer, Set<UserKey>> usersNotifiedPerServer) {
            this.recipient = recipient;
            this.usersNotifiedPerServer = usersNotifiedPerServer;
            this.userKey = recipient.getUserKey();
        }

        @Override
        public boolean isSuccessfulSendForRecipient() {
            return this.successfulSendForRecipient;
        }

        @Override
        public boolean isAnyEnabledServers() {
            return this.anyEnabledServers;
        }

        private void failed() {
            this.successfulSendForRecipient = false;
        }

        @Override
        public SendResult send() {
            for (ServerConfiguration serverConfig : IndividualNotificationTask.this.recipientPreferences.getServers(this.userKey)) {
                Message message;
                NotificationAddress address;
                Server server;
                NotificationMedium notificationMedium;
                int serverId;
                block8: {
                    NotificationError error;
                    serverId = serverConfig.getId();
                    notificationMedium = serverConfig.getNotificationMedium();
                    try {
                        if (this.usersNotifiedPerServer.containsKey(serverId) && this.usersNotifiedPerServer.get(serverId).contains(this.userKey)) continue;
                        server = IndividualNotificationTask.this.components.getServerFactory().getServer(serverConfig);
                        if (notificationMedium == null || server == null || !notificationMedium.isUserConfigured(this.recipient.getUserKey()) || !IndividualNotificationTask.this.recipientPreferences.shouldSend(this.recipient, IndividualNotificationTask.this.event.getAuthor(), serverConfig)) continue;
                        this.anyEnabledServers = true;
                        Option<Map<String, Object>> context = IndividualNotificationTask.this.components.getRenderContextFactory().createContext(IndividualNotificationTask.this.event, serverConfig, (Either<NotificationAddress, RoleRecipient>)Either.right((Object)this.recipient));
                        if (context.isEmpty()) continue;
                        address = IndividualNotificationTask.this.resolveAddressFor(this.recipient, (Map)context.get(), serverConfig);
                        if (address.getAddressData().isEmpty()) {
                            error = new NotificationError(String.format("Address failed to resolve for server '%s' with id '%d' on medium '%s' for recipient '%s'.", serverConfig.getServerName(), serverConfig.getId(), notificationMedium.getKey(), this.recipient));
                            IndividualNotificationTask.this.components.getErrorRegistry().addError(serverId, IndividualNotificationTask.this, error);
                            continue;
                        }
                        message = notificationMedium.renderMessage(RecipientType.INDIVIDUAL, (Map)context.get(), serverConfig);
                        if (message == null) {
                            error = new NotificationError(String.format("Message failed to render for server '%s' on medium '%s' for user '%s'.", serverConfig.getServerName(), notificationMedium.getKey(), this.userKey));
                            IndividualNotificationTask.this.components.getErrorRegistry().addError(serverId, IndividualNotificationTask.this, error);
                        }
                        break block8;
                    }
                    catch (Throwable throwable) {
                        error = new NotificationError(String.format("Error generating message for server '%s' on medium '%s' for user '%s'.", serverConfig.getServerName(), notificationMedium.getKey(), this.userKey), throwable);
                        IndividualNotificationTask.this.components.getErrorRegistry().addError(serverId, IndividualNotificationTask.this, error);
                        this.failed();
                    }
                    continue;
                }
                try {
                    server.sendIndividualNotification(address, message);
                    IndividualNotificationTask.this.components.getErrorRegistry().logSuccess(serverId);
                    if (!this.usersNotifiedPerServer.containsKey(serverId)) {
                        HashSet users = Sets.newHashSet();
                        this.usersNotifiedPerServer.put(serverId, users);
                    }
                    this.usersNotifiedPerServer.get(serverId).add(this.userKey);
                    IndividualNotificationSentEvent analyticsEvent = new IndividualNotificationSentEvent(IndividualNotificationTask.this.event.getKey(), notificationMedium.getKey());
                    IndividualNotificationTask.this.components.getAnalyticsPublisher().publishEvent(analyticsEvent, (Option<UserKey>)Option.some((Object)this.userKey));
                }
                catch (NotificationException e) {
                    this.failed();
                    IndividualNotificationTask.this.components.getErrorRegistry().addError(serverId, IndividualNotificationTask.this, new NotificationError("Error sending to individual '" + this.userKey + "' on server '" + serverConfig.getServerName() + "'", e));
                }
            }
            return this;
        }
    }
}

