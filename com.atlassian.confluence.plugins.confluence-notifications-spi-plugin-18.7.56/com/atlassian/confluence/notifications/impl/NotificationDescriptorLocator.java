/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.notifications.impl;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.Participant;
import com.atlassian.confluence.notifications.impl.descriptors.AbstractParticipantDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTemplateDescriptor;
import com.atlassian.confluence.notifications.impl.descriptors.NotificationTransformerDescriptor;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.Optional;

public interface NotificationDescriptorLocator {
    public Iterable<NotificationTransformerDescriptor> findPayloadTransformerDescriptors(Object var1);

    public <PAYLOAD extends NotificationPayload> Maybe<NotificationDescriptor<PAYLOAD>> findNotificationDescriptor(PAYLOAD var1, ModuleCompleteKey var2);

    public <P extends Participant> Iterable<? extends AbstractParticipantDescriptor<P>> findParticipantDescriptors(Class<P> var1);

    @Deprecated
    public Maybe<NotificationTemplateDescriptor> findTemplateDescriptor(Notification var1, String var2);

    default public Optional<NotificationTemplateDescriptor> findOptionalTemplateDescriptor(Notification notification, String mediumKey) {
        return Optional.ofNullable((NotificationTemplateDescriptor)((Object)this.findTemplateDescriptor(notification, mediumKey).getOrNull()));
    }
}

