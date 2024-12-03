/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.google.common.base.Predicate
 *  javax.mail.Address
 *  javax.mail.Message$RecipientType
 *  javax.mail.MessagingException
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.mail.InboundMailServer;
import com.google.common.base.Predicate;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MessageRecipientFilter
implements Predicate<MimeMessage> {
    private final String expectedAddress;

    public MessageRecipientFilter(InboundMailServer inboundMailServer) {
        this.expectedAddress = inboundMailServer.getToAddress();
    }

    public boolean apply(MimeMessage input) {
        try {
            Address[] recipients = input.getRecipients(Message.RecipientType.TO);
            if (recipients == null) {
                return false;
            }
            for (Address recipient : recipients) {
                String actualAddress = ((InternetAddress)recipient).getAddress();
                if (!actualAddress.equals(this.expectedAddress)) continue;
                return true;
            }
        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}

