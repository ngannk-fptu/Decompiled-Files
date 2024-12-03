/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.embed.MimeBodyPartReference
 *  com.atlassian.plugin.notifications.api.medium.Message
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.plugin.notifications.api.medium.Message;

public interface MimeMultipartMessage
extends Message {
    public Iterable<MimeBodyPartReference> getRelatedBodyPartReferences();
}

