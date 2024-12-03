/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.atlassian.plugin.notifications.api.event.NotificationEvent
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.api.queue.NotificationQueueManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSortedSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.DispatchService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.ProductionAwareLoggerSwitch;
import com.atlassian.confluence.notifications.RecipientsProvider;
import com.atlassian.confluence.notifications.impl.NotificationDescriptorLocator;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.fugue.Pair;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.api.queue.NotificationQueueManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

final class NotificationQueueDispatchService
implements DispatchService {
    private static final ProductionAwareLoggerSwitch log = ProductionAwareLoggerSwitch.forClass(NotificationQueueDispatchService.class);
    private final NotificationDescriptorLocator descriptorLocator;
    private final NotificationQueueManager queueManager;
    private final TransactionTemplate transactionTemplate;

    public NotificationQueueDispatchService(NotificationDescriptorLocator descriptorLocator, NotificationQueueManager queueManager, TransactionTemplate transactionTemplate) {
        this.descriptorLocator = descriptorLocator;
        this.queueManager = queueManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public void dispatch(Notification notification) {
        this.dispatchWithAdditionalRecipients(notification, Collections.EMPTY_LIST);
    }

    @Override
    public void dispatchWithAdditionalRecipients(Notification notification, Iterable<RoleRecipient> additionalRecipients) {
        NotificationDescriptor notificationDescriptor = (NotificationDescriptor)((Object)this.descriptorLocator.findNotificationDescriptor(notification.getPayload(), notification.getKey()).get());
        Iterable<RecipientsProvider> recipientsProviders = this.findRecipientProviders(notification);
        if (Iterables.isEmpty(recipientsProviders) && Iterables.isEmpty(additionalRecipients)) {
            log.warnOrDebug("No additional recipients were provided and no recipient providers for notification [%s] were found, thus aborting dispatch.", notification);
            return;
        }
        Callable<Iterable<RoleRecipient>> computeUserRecipients = () -> this.compileDistinctRecipients(Iterables.concat(this.collectUserBasedRecipients(notification, recipientsProviders), (Iterable)additionalRecipients));
        Callable<Iterable<NotificationAddress>> computeNonUserRecipients = () -> this.collectNonUserBasedRecipients(notification, recipientsProviders);
        this.dispatchForRecipients(notification, notificationDescriptor, computeUserRecipients, computeNonUserRecipients, (RecipientsProvider[])Iterables.toArray(recipientsProviders, RecipientsProvider.class));
    }

    private Iterable<NotificationAddress> collectNonUserBasedRecipients(Notification notification, Iterable<RecipientsProvider> recipientsProviders) {
        return Iterables.concat((Iterable)Iterables.transform(recipientsProviders, provider -> {
            try {
                return provider.nonUserBasedRecipientsFor(notification);
            }
            catch (RuntimeException e) {
                log.errorOrDebug(e);
                return Collections.EMPTY_LIST;
            }
        }));
    }

    @Override
    public void dispatchForExclusiveRecipients(Notification notification, Iterable<RoleRecipient> exlusiveRecipients) {
        NotificationDescriptor notificationDescriptor = (NotificationDescriptor)((Object)this.descriptorLocator.findNotificationDescriptor(notification.getPayload(), notification.getKey()).get());
        Callable<Iterable<RoleRecipient>> computeUserRecipients = () -> this.compileDistinctRecipients(exlusiveRecipients);
        Callable<Iterable<NotificationAddress>> computeNonUserRecipients = () -> Collections.EMPTY_LIST;
        this.dispatchForRecipients(notification, notificationDescriptor, computeUserRecipients, computeNonUserRecipients, new RecipientsProvider[0]);
    }

    private void dispatchForRecipients(Notification notification, NotificationDescriptor descriptor, Callable<Iterable<RoleRecipient>> computeUserRecipients, Callable<Iterable<NotificationAddress>> computeNonUserRecipients, RecipientsProvider ... recipientsProviders) {
        try {
            Pair r = (Pair)this.transactionTemplate.execute(() -> {
                try {
                    Iterable userRecipients = (Iterable)computeUserRecipients.call();
                    Iterable nonUserRecipients = (Iterable)computeNonUserRecipients.call();
                    return Pair.pair((Object)userRecipients, (Object)nonUserRecipients);
                }
                catch (Exception e) {
                    log.errorOrDebug(e, "Error computing recipients", new Object[0]);
                    return Pair.pair(Collections.emptyList(), Collections.emptyList());
                }
            });
            Iterable userRecipients = (Iterable)r.left();
            Iterable nonUserRecipients = (Iterable)r.right();
            if (Iterables.isEmpty((Iterable)userRecipients) && Iterables.isEmpty((Iterable)nonUserRecipients)) {
                log.warnOrDebug("No recipients were compiled for notification [%s] from the following list of providers [%s], thus aborting dispatch.", notification, ToStringBuilder.reflectionToString((Object)recipientsProviders, (ToStringStyle)ToStringStyle.SIMPLE_STYLE));
                return;
            }
            NotificationEvent notificationEvent = descriptor.getNotificationEventFactory().create(notification);
            if (!Iterables.isEmpty((Iterable)userRecipients)) {
                this.queueManager.submitIndividualNotification(userRecipients, notificationEvent);
            }
            if (!Iterables.isEmpty((Iterable)nonUserRecipients)) {
                this.queueManager.submitIndividualNotificationViaAddress(nonUserRecipients, notificationEvent);
            }
        }
        catch (IllegalStateException ex) {
            log.errorOrDebug(ex, "Error submitting email for generation, check your payload class is serialisable.", new Object[0]);
        }
        catch (Exception ex) {
            log.errorOrDebug(ex, "Error submitting email for generation", new Object[0]);
        }
    }

    private Iterable<UserKeyRoleRecipient> collectUserBasedRecipients(Notification notification, Iterable<RecipientsProvider> notificationRecipientsProviders) {
        return Iterables.concat((Iterable)Iterables.transform(notificationRecipientsProviders, provider -> {
            try {
                return provider.userBasedRecipientsFor(notification);
            }
            catch (RuntimeException e) {
                log.errorOrDebug(e);
                return Collections.EMPTY_LIST;
            }
        }));
    }

    private <T extends RoleRecipient> Iterable<T> compileDistinctRecipients(Iterable<T> recipients) {
        Comparator compareLexographically = (o1, o2) -> o1.getUserKey().getStringValue().compareTo(o2.getUserKey().getStringValue());
        return ImmutableSortedSet.copyOf((Comparator)compareLexographically, (Iterable)Iterables.filter(recipients, recipient -> UserKeyRoleRecipient.UNKNOWN != recipient));
    }

    private Iterable<RecipientsProvider> findRecipientProviders(Notification notification) {
        Object payload = notification.getPayload();
        Iterable<AbstractParticipantDescriptor<RecipientsProvider>> allRecipientProviders = this.descriptorLocator.findParticipantDescriptors(RecipientsProvider.class);
        return Iterables.filter((Iterable)Iterables.transform(allRecipientProviders, (Function)new Function<AbstractParticipantDescriptor<RecipientsProvider>, RecipientsProvider>(){

            public RecipientsProvider apply(@Nullable AbstractParticipantDescriptor<RecipientsProvider> descriptor) {
                return (RecipientsProvider)descriptor.getModule();
            }
        }), provider -> provider.getPayloadType().isAssignableFrom(payload.getClass()));
    }
}

