/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.LinkedHashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.activation.DataSource
 *  javax.mail.Address
 *  javax.mail.Header
 *  javax.mail.MessagingException
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.mail.util.MimeMessageParser
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.EmailBody;
import com.atlassian.confluence.plugins.emailgateway.api.EmailBodyType;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHeaders;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.api.SerializableAttachment;
import com.atlassian.confluence.plugins.emailgateway.service.ReceivedEmailMimeConverter;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.util.MimeMessageParser;

public class DefaultReceivedEmailMimeConverter
implements ReceivedEmailMimeConverter {
    @Override
    public ReceivedEmail convertMimeMessage(MimeMessage mimeMessage, InternetAddress recipient) throws Exception {
        MimeMessageParser mimeMessageParser = new MimeMessageParser(mimeMessage).parse();
        EmailBody body = this.extractBody(mimeMessageParser);
        List<SerializableAttachment> attachments = this.extractAttachments(mimeMessageParser);
        InternetAddress sender = new InternetAddress(mimeMessageParser.getFrom());
        List<InternetAddress> participants = this.extractParticipants(recipient, mimeMessageParser);
        EmailHeaders headers = this.extractHeaders(mimeMessage.getAllHeaders());
        String subject = mimeMessageParser.getSubject();
        EmailBodyType bodyType = body.getType();
        String content = body.getContent();
        Map<String, ? extends Serializable> context = body.getContext();
        return new ReceivedEmail(sender, recipient, participants, headers, subject, bodyType, content, attachments, context);
    }

    private List<SerializableAttachment> extractAttachments(MimeMessageParser mimeMessageParser) throws MessagingException {
        ArrayList<SerializableAttachment> attachments = new ArrayList<SerializableAttachment>();
        if (mimeMessageParser.hasAttachments()) {
            try {
                for (DataSource attachment : mimeMessageParser.getAttachmentList()) {
                    attachments.add(new SerializableAttachment(IOUtils.toByteArray((InputStream)attachment.getInputStream()), attachment.getContentType(), attachment.getName()));
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return attachments;
    }

    private EmailBody extractBody(MimeMessageParser mimeMessageParser) throws MessagingException {
        try {
            Map<Object, Object> context = Collections.emptyMap();
            if (!mimeMessageParser.getAttachmentList().isEmpty()) {
                HashMap<String, String> attachmentCidToNames = new HashMap<String, String>();
                for (String cid : mimeMessageParser.getContentIds()) {
                    DataSource attachment = mimeMessageParser.findAttachmentByCid(cid);
                    if (attachment == null) continue;
                    attachmentCidToNames.put(cid, attachment.getName());
                }
                if (!attachmentCidToNames.isEmpty()) {
                    context = Collections.singletonMap("attachmentCidToName", attachmentCidToNames);
                }
            }
            EmailBody emailBody = mimeMessageParser.hasHtmlContent() ? new EmailBody(EmailBodyType.HTML, mimeMessageParser.getHtmlContent(), context) : new EmailBody(EmailBodyType.TEXT, mimeMessageParser.getPlainContent(), context);
            return emailBody;
        }
        catch (Exception e) {
            throw new MessagingException("Failed to extract email body content", e);
        }
    }

    List<InternetAddress> extractParticipants(InternetAddress recipient, MimeMessageParser mimeMessageParser) throws Exception {
        ArrayList participants = Lists.newArrayList();
        this.addParticipants(participants, mimeMessageParser.getTo());
        this.addParticipants(participants, mimeMessageParser.getCc());
        participants.remove(recipient);
        return participants;
    }

    private void addParticipants(List<InternetAddress> participants, List<Address> mimeMessageRecipients) {
        if (mimeMessageRecipients != null) {
            for (Address recipient : mimeMessageRecipients) {
                participants.add((InternetAddress)recipient);
            }
        }
    }

    private EmailHeaders extractHeaders(Enumeration mimeHeaderEnumeration) {
        LinkedHashMultimap headers = LinkedHashMultimap.create();
        while (mimeHeaderEnumeration.hasMoreElements()) {
            Header header = (Header)mimeHeaderEnumeration.nextElement();
            headers.put((Object)header.getName(), (Object)header.getValue());
        }
        LinkedHashMap result = Maps.newLinkedHashMap();
        for (Map.Entry entry : headers.asMap().entrySet()) {
            String headerName = (String)entry.getKey();
            result.put(headerName, Lists.newArrayList((Iterable)((Iterable)entry.getValue())));
        }
        return new EmailHeaders(result);
    }
}

