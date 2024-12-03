/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
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
import com.atlassian.plugin.notifications.dispatcher.NotificationError;
import com.atlassian.plugin.notifications.dispatcher.NotificationQueueMonitor;
import com.atlassian.plugin.notifications.dispatcher.TaskComponents;
import com.atlassian.plugin.notifications.dispatcher.task.SendResult;
import com.atlassian.plugin.notifications.dispatcher.task.Sender;
import com.atlassian.plugin.notifications.dispatcher.task.events.IndividualNotificationSentEvent;
import com.atlassian.plugin.notifications.dispatcher.util.DeliveryStatus;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class AddressesNotificationTask
extends AbstractNotificationTask {
    private final Iterable<NotificationAddress> addresses;

    public AddressesNotificationTask(Iterable<NotificationAddress> addresses, TaskComponents components, NotificationEvent event, NotificationQueueMonitor monitor) {
        super(components, event, RecipientType.INDIVIDUAL, monitor);
        this.addresses = Lists.newArrayList(addresses);
    }

    @Override
    public DeliveryStatus execute() {
        this.setState(TaskStatus.State.SENDING);
        Iterator<NotificationAddress> iterator = this.addresses.iterator();
        while (iterator.hasNext()) {
            NotificationAddress address = iterator.next();
            SendResult sendResult = new CustomAddressSender(address).send();
            if (!sendResult.isSuccessfulSendForRecipient() && sendResult.isAnyEnabledServers()) continue;
            iterator.remove();
        }
        if (Iterables.size(this.addresses) > 0) {
            return DeliveryStatus.ERROR;
        }
        return DeliveryStatus.SUCCESS;
    }

    @Override
    public List<RecipientDescription> getRecipientDescriptions(I18nResolver i18n) {
        return null;
    }

    private class CustomAddressSender
    implements Sender,
    SendResult {
        private boolean successfulSendForRecipient = false;
        private boolean anyEnabledServers = false;
        private final NotificationAddress currentAddress;

        public CustomAddressSender(NotificationAddress currentAddress) {
            this.currentAddress = currentAddress;
        }

        @Override
        public SendResult send() {
            if (this.currentAddress.getAddressData().isEmpty()) {
                AddressesNotificationTask.this.components.getErrorRegistry().addError(-1, AddressesNotificationTask.this, new NotificationError("Address '" + this.currentAddress + "' does not have any data."));
                return this;
            }
            Iterable applicableServers = Iterables.filter(AddressesNotificationTask.this.components.getServerManager().getServers(), (Predicate)new Predicate<ServerConfiguration>(){

                public boolean apply(@Nullable ServerConfiguration input) {
                    return input.getNotificationMedium().getKey().equals(CustomAddressSender.this.currentAddress.getMediumKey().get());
                }
            });
            if (Iterables.isEmpty((Iterable)applicableServers)) {
                AddressesNotificationTask.this.components.getErrorRegistry().addError(-1, AddressesNotificationTask.this, new NotificationError("The address '" + this.currentAddress + "' did not match any existing notification medium keys. Not sending."));
                return this;
            }
            for (ServerConfiguration serverConfig : applicableServers) {
                Message message;
                Server server;
                NotificationMedium notificationMedium;
                block8: {
                    NotificationError error;
                    notificationMedium = serverConfig.getNotificationMedium();
                    try {
                        server = AddressesNotificationTask.this.components.getServerFactory().getServer(serverConfig);
                        if (notificationMedium == null || server == null) continue;
                        this.anyEnabledServers = true;
                        Option<Map<String, Object>> context = AddressesNotificationTask.this.components.getRenderContextFactory().createContext(AddressesNotificationTask.this.event, serverConfig, (Either<NotificationAddress, RoleRecipient>)Either.left((Object)this.currentAddress));
                        if (context.isEmpty()) continue;
                        message = notificationMedium.renderMessage(RecipientType.INDIVIDUAL, (Map)context.get(), serverConfig);
                        if (message == null) {
                            error = new NotificationError(String.format("Message failed to render for server '%s' on medium '%s' for address '%s'.", serverConfig.getServerName(), notificationMedium.getKey(), this.currentAddress));
                            AddressesNotificationTask.this.components.getErrorRegistry().addError(serverConfig.getId(), AddressesNotificationTask.this, error);
                        }
                        break block8;
                    }
                    catch (Throwable throwable) {
                        error = new NotificationError(String.format("Message failed to render for server '%s' on medium '%s' for address '%s'.", serverConfig.getServerName(), notificationMedium.getKey(), this.currentAddress), throwable);
                        AddressesNotificationTask.this.components.getErrorRegistry().addError(serverConfig.getId(), AddressesNotificationTask.this, error);
                    }
                    continue;
                }
                try {
                    server.sendIndividualNotification(this.currentAddress, message);
                    AddressesNotificationTask.this.components.getErrorRegistry().logSuccess(serverConfig.getId());
                    this.successfulSendForRecipient = true;
                    IndividualNotificationSentEvent analyticsEvent = new IndividualNotificationSentEvent(AddressesNotificationTask.this.event.getKey(), notificationMedium.getKey());
                    AddressesNotificationTask.this.components.getAnalyticsPublisher().publishEvent(analyticsEvent, (Option<UserKey>)Option.none(UserKey.class));
                }
                catch (NotificationException e) {
                    AddressesNotificationTask.this.components.getErrorRegistry().addError(serverConfig.getId(), AddressesNotificationTask.this, new NotificationError("Error sending to address '" + this.currentAddress + "' on server '" + serverConfig.getServerName() + "'", e));
                }
            }
            return this;
        }

        @Override
        public boolean isSuccessfulSendForRecipient() {
            return this.successfulSendForRecipient;
        }

        @Override
        public boolean isAnyEnabledServers() {
            return this.anyEnabledServers;
        }
    }
}

