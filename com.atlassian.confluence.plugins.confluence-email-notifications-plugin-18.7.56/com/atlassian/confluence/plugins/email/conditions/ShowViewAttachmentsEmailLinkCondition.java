/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentBatchUploadCompletedEvent
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 */
package com.atlassian.confluence.plugins.email.conditions;

import com.atlassian.confluence.event.events.content.attachment.AttachmentBatchUploadCompletedEvent;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;

public class ShowViewAttachmentsEmailLinkCondition
extends AbstractNotificationCondition {
    protected boolean shouldDisplay(NotificationContext context) {
        return context.getEvent() instanceof AttachmentBatchUploadCompletedEvent;
    }
}

