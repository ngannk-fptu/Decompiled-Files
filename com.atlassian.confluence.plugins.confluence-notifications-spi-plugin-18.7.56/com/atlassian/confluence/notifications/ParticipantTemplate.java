/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassUtils
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.notifications;

import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.NotificationPayload;
import com.atlassian.confluence.notifications.Participant;
import com.atlassian.plugin.util.ClassUtils;
import com.google.common.base.Preconditions;

public abstract class ParticipantTemplate<PAYLOAD extends NotificationPayload>
implements Participant<PAYLOAD> {
    protected final Class<PAYLOAD> payloadType = (Class)ClassUtils.getTypeArguments(ParticipantTemplate.class, this.getClass()).get(0);

    public ParticipantTemplate() {
        Preconditions.checkNotNull(this.payloadType, (String)"[%s] did not convey its type argument as expected. It should have been parameterized with a type as a first argument indicating the notification payload it adheres to.", (Object)this.getClass().getName());
    }

    @Override
    public final Class<PAYLOAD> getPayloadType() {
        return this.payloadType;
    }

    protected void verifyPayloadMatches(Notification<PAYLOAD> notification) throws IllegalArgumentException {
        Preconditions.checkNotNull(notification, (Object)"Given notification argument is null.");
        Preconditions.checkArgument((boolean)this.payloadType.isAssignableFrom(notification.getPayload().getClass()), (String)"The payload of the given notification is of type [%s] which is not a subtype of the notification payload [%s].", notification.getClass(), (Object)this.payloadType.getName());
    }
}

