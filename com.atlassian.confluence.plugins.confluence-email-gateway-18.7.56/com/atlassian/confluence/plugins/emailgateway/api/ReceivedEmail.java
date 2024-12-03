/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.plugins.emailgateway.api.EmailBodyType;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHeaders;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.mail.internet.InternetAddress;

@PublicApi
public class ReceivedEmail
implements Serializable {
    private String bodyContent;
    private EmailBodyType bodyType;
    private InternetAddress sender;
    private InternetAddress recipientAddress;
    private List<InternetAddress> participants;
    private EmailHeaders headers;
    private String subject;
    private List<SerializableAttachment> attachments;
    private Map<String, ? extends Serializable> context;

    private ReceivedEmail() {
    }

    public ReceivedEmail(InternetAddress sender, InternetAddress recipientAddress, List<InternetAddress> participants, EmailHeaders headers, String subject, EmailBodyType bodyType, String bodyContent, List<SerializableAttachment> attachments) {
        this.bodyContent = (String)Preconditions.checkNotNull((Object)bodyContent);
        this.bodyType = (EmailBodyType)((Object)Preconditions.checkNotNull((Object)((Object)bodyType)));
        this.sender = (InternetAddress)Preconditions.checkNotNull((Object)sender);
        this.recipientAddress = (InternetAddress)Preconditions.checkNotNull((Object)recipientAddress);
        this.subject = (String)Preconditions.checkNotNull((Object)subject);
        this.headers = (EmailHeaders)Preconditions.checkNotNull((Object)headers);
        this.participants = (List)Preconditions.checkNotNull(participants);
        this.attachments = Lists.newArrayList(attachments);
        this.context = Maps.newHashMap();
    }

    public ReceivedEmail(InternetAddress sender, InternetAddress recipient, List<InternetAddress> participants, EmailHeaders headers, String subject, EmailBodyType bodyType, String content, List<SerializableAttachment> attachments, Map<String, ? extends Serializable> context) {
        this(sender, recipient, participants, headers, subject, bodyType, content, attachments);
        this.context = Maps.newHashMap(context);
    }

    public String getBodyContentAsString() {
        return this.bodyContent;
    }

    public List<InternetAddress> getParticipants() {
        return Collections.unmodifiableList(this.participants);
    }

    public InternetAddress getSender() {
        return this.sender;
    }

    public EmailBodyType getBodyType() {
        return this.bodyType;
    }

    public InternetAddress getRecipientAddress() {
        return this.recipientAddress;
    }

    public EmailHeaders getHeaders() {
        return this.headers;
    }

    public String getSubject() {
        return this.subject;
    }

    public List<SerializableAttachment> getAttachments() {
        return this.attachments;
    }

    public Map<String, ? extends Serializable> getContext() {
        return this.context;
    }
}

