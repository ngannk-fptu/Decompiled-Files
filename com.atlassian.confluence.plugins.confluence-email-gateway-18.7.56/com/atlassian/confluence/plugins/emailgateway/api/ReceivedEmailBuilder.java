/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.confluence.plugins.emailgateway.api.EmailBodyType;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHeaders;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class ReceivedEmailBuilder {
    private InternetAddress sender = ReceivedEmailBuilder.address("sender@example.com");
    private InternetAddress recipientAdress = ReceivedEmailBuilder.address("recipient@example.com");
    private List<InternetAddress> participants = Lists.newArrayList();
    private Map<String, List<String>> headers = Maps.newHashMap();
    private String subject = "Pageify Me!";
    private EmailBodyType bodyType = EmailBodyType.TEXT;
    private String content = "I want to be a page, baby";
    private List<SerializableAttachment> attachments = ImmutableList.of();
    private Map<String, ? extends Serializable> context = Maps.newHashMap();

    public ReceivedEmail build() {
        return new ReceivedEmail(this.sender, this.recipientAdress, this.participants, new EmailHeaders(this.headers), this.subject, this.bodyType, this.content, this.attachments, this.context);
    }

    public static ReceivedEmailBuilder receivedEmail() {
        return new ReceivedEmailBuilder();
    }

    private static final InternetAddress address(String address) {
        try {
            return new InternetAddress(address);
        }
        catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    public ReceivedEmailBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public ReceivedEmailBuilder withSender(String senderAddress) {
        this.sender = ReceivedEmailBuilder.address(senderAddress);
        return this;
    }

    public ReceivedEmailBuilder withBodyContent(String bodyContent) {
        this.content = bodyContent;
        return this;
    }

    public ReceivedEmailBuilder withHeader(String headerName, String headerValue) {
        this.headers.put(headerName, Lists.newArrayList((Object[])new String[]{headerValue}));
        return this;
    }

    public ReceivedEmailBuilder withParticipants(String ... participants) {
        for (String participantAddress : participants) {
            this.participants.add(ReceivedEmailBuilder.address(participantAddress));
        }
        return this;
    }

    public ReceivedEmailBuilder withContext(Map<String, ? extends Serializable> context) {
        this.context = context;
        return this;
    }

    public ReceivedEmailBuilder withAttachments(List<SerializableAttachment> attachments) {
        this.attachments = attachments;
        return this;
    }
}

