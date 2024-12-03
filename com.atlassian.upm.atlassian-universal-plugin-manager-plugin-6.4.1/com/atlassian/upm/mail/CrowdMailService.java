/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.mail.MailManager
 *  com.atlassian.crowd.manager.mail.MailSendException
 *  com.atlassian.crowd.manager.mail.TextEmailMessage
 *  com.atlassian.crowd.manager.mail.TextEmailMessage$Builder
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  com.atlassian.crowd.manager.property.PropertyManagerException
 *  com.atlassian.sal.api.user.UserKey
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.upm.mail;

import com.atlassian.crowd.manager.mail.MailManager;
import com.atlassian.crowd.manager.mail.MailSendException;
import com.atlassian.crowd.manager.mail.TextEmailMessage;
import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.crowd.manager.property.PropertyManagerException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.mail.ProductMailService;
import com.atlassian.upm.mail.UpmEmail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class CrowdMailService
implements ProductMailService {
    private final MailManager mailManager;
    private final PropertyManager propertyManager;

    public CrowdMailService(MailManager mailManager, PropertyManager propertyManager) {
        this.mailManager = mailManager;
        this.propertyManager = propertyManager;
    }

    @Override
    public void sendMail(UpmEmail upmEmail) {
        try {
            this.mailManager.sendEmails(Collections.singleton(this.convert(upmEmail)));
        }
        catch (MailSendException | AddressException e) {
            throw new RuntimeException("Couldn't send e-mail", e);
        }
    }

    private TextEmailMessage convert(UpmEmail upmEmail) throws AddressException {
        TextEmailMessage.Builder builder = TextEmailMessage.builder().setSubject(upmEmail.getSubject()).setBody(upmEmail.getBody());
        for (String from : upmEmail.getFrom()) {
            builder.setFrom(new InternetAddress(from));
        }
        builder.setTo(this.asInternetAddresses(upmEmail.getTo()));
        builder.setCc(this.asInternetAddresses(upmEmail.getCc()));
        builder.setBcc(this.asInternetAddresses(upmEmail.getBcc()));
        builder.setReplyTo(this.asInternetAddresses(upmEmail.getReplyTo()));
        HashMap<String, String> headers = new HashMap<String, String>(upmEmail.getHeaders());
        for (String messageId : upmEmail.getMessageId()) {
            headers.put("Message-ID", messageId);
        }
        for (String inReplyTo : upmEmail.getInReplyTo()) {
            headers.put("In-Reply-To", inReplyTo);
        }
        builder.setHeaders(headers);
        return builder.build();
    }

    private Collection<InternetAddress> asInternetAddresses(Collection<String> addresses) throws AddressException {
        ArrayList<InternetAddress> internetAddresses = new ArrayList<InternetAddress>();
        for (String address : addresses) {
            internetAddresses.add(new InternetAddress(address));
        }
        return internetAddresses;
    }

    @Override
    public UpmEmail.Format getUserEmailFormatPreference(UserKey userKey) {
        return UpmEmail.Format.TEXT;
    }

    @Override
    public Option<String> getInstanceName() {
        try {
            return Option.option(this.propertyManager.getDeploymentTitle());
        }
        catch (PropertyManagerException e) {
            return Option.none();
        }
    }

    @Override
    public boolean isConfigured() {
        return this.mailManager.isConfigured();
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}

