/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.api.medium.recipient.RecipientRepresentation;

public interface NotificationRecipientProvider {
    public Iterable<RecipientRepresentation> getAllRecipients();
}

