/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  javax.mail.internet.InternetAddress
 *  org.joda.time.Instant
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHeaders;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.List;
import javax.mail.internet.InternetAddress;
import org.joda.time.Instant;

@PublicApi
public class StagedEmailThread
implements Serializable {
    private StagedEmailThreadKey stagedEmailThreadKey;
    private String spaceKey;
    private ReceivedEmail receivedEmail;
    private long stagingDateMillis;

    private StagedEmailThread() {
    }

    public StagedEmailThread(StagedEmailThreadKey stagedEmailThreadKey, String spaceKey, ReceivedEmail receivedEmail) {
        this.spaceKey = (String)Preconditions.checkNotNull((Object)spaceKey);
        this.stagedEmailThreadKey = (StagedEmailThreadKey)Preconditions.checkNotNull((Object)stagedEmailThreadKey);
        this.receivedEmail = (ReceivedEmail)Preconditions.checkNotNull((Object)receivedEmail);
        this.stagingDateMillis = new Instant().getMillis();
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getBodyContentAsString() {
        return this.getReceivedEmail().getBodyContentAsString();
    }

    public ReceivedEmail getReceivedEmail() {
        return this.receivedEmail;
    }

    public List<InternetAddress> getParticipants() {
        return this.getReceivedEmail().getParticipants();
    }

    public InternetAddress getSender() {
        return this.getReceivedEmail().getSender();
    }

    public String getSubject() {
        return this.getReceivedEmail().getSubject();
    }

    public EmailHeaders getHeaders() {
        return this.getReceivedEmail().getHeaders();
    }

    public List<SerializableAttachment> getAttachments() {
        return this.getReceivedEmail().getAttachments();
    }

    public StagedEmailThreadKey getKey() {
        return this.stagedEmailThreadKey;
    }

    public Instant getStagingDate() {
        return new Instant(this.stagingDateMillis);
    }
}

