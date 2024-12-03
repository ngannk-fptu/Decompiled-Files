/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  javax.mail.Message$RecipientType
 *  javax.mail.internet.InternetAddress
 */
package com.atlassian.crowd.manager.mail;

import com.atlassian.crowd.manager.mail.EmailMessage;
import com.atlassian.fugue.Pair;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

public class MailSendException
extends Exception {
    private static final String MAIL_SEND_EXCEPTION_MESSAGE = "Could not send email to ";

    @Deprecated
    public MailSendException(InternetAddress email) {
        super(MAIL_SEND_EXCEPTION_MESSAGE + email);
    }

    @Deprecated
    public MailSendException(InternetAddress email, Throwable cause) {
        super(MAIL_SEND_EXCEPTION_MESSAGE + email + ": " + cause.getMessage(), cause);
    }

    public MailSendException(EmailMessage email) {
        super(MailSendException.errorMessage(email));
    }

    public MailSendException(EmailMessage email, Throwable cause) {
        super(MailSendException.errorMessage(email) + ": " + cause.getMessage(), cause);
    }

    public MailSendException(Throwable cause) {
        super(cause);
    }

    private static String errorMessage(EmailMessage email) {
        ArrayList recipients = new ArrayList();
        email.getTo().forEach(recipient -> recipients.add(Pair.pair((Object)recipient, (Object)Message.RecipientType.TO)));
        email.getCc().forEach(recipient -> recipients.add(Pair.pair((Object)recipient, (Object)Message.RecipientType.CC)));
        email.getBcc().forEach(recipient -> recipients.add(Pair.pair((Object)recipient, (Object)Message.RecipientType.BCC)));
        return String.format("Could not send email to %d recipient(s) (%s)", recipients.size(), recipients.stream().map(recipientInfo -> String.format("%s: %s", recipientInfo.right(), recipientInfo.left())).collect(Collectors.joining(", ")));
    }
}

