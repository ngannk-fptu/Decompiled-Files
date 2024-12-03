/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.embed.MimeBodyPartReference
 *  com.atlassian.plugin.notifications.api.medium.Message
 *  com.atlassian.sal.api.user.UserProfile
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.confluence.mail.embed.MimeBodyPartReference;
import com.atlassian.confluence.plugins.email.medium.MimeMultipartMessage;
import com.atlassian.plugin.notifications.api.medium.Message;
import com.atlassian.sal.api.user.UserProfile;
import java.util.Map;

public class MimeMultipartMessageDecorator
implements MimeMultipartMessage {
    private final Message delegate;
    private final Iterable<MimeBodyPartReference> relatedBodyPartReferences;

    public MimeMultipartMessageDecorator(Message delegate, Iterable<MimeBodyPartReference> relatedBodyPartReferences) {
        this.delegate = delegate;
        this.relatedBodyPartReferences = relatedBodyPartReferences;
    }

    public String getMessageId() {
        return this.delegate.getMessageId();
    }

    public String getSubject() {
        return this.delegate.getSubject();
    }

    public String getBody() {
        return this.delegate.getBody();
    }

    public Map<String, Object> getMetadata() {
        return this.delegate.getMetadata();
    }

    public UserProfile getOriginatingUser() {
        return this.delegate.getOriginatingUser();
    }

    @Override
    public Iterable<MimeBodyPartReference> getRelatedBodyPartReferences() {
        return this.relatedBodyPartReferences;
    }
}

