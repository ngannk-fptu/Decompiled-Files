/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.mail.server.MailServer
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.plugins.emailgateway.polling.MimeMessageTransformer;
import com.atlassian.confluence.plugins.emailgateway.service.ReceivedEmailMimeConverter;
import com.atlassian.mail.server.MailServer;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class DefaultMimeMessageTransformer
implements MimeMessageTransformer {
    private final ReceivedEmailMimeConverter receivedEmailMimeConverter;

    public DefaultMimeMessageTransformer(ReceivedEmailMimeConverter receivedEmailMimeConverter) {
        this.receivedEmailMimeConverter = receivedEmailMimeConverter;
    }

    @Override
    public ReceivedEmail transformMimeMessage(MimeMessage mimeMessage, MailServer mailServer) throws Exception {
        InboundMailServer inboundMailServer = (InboundMailServer)mailServer;
        InternetAddress recipientAddress = new InternetAddress(inboundMailServer.getToAddress());
        return this.receivedEmailMimeConverter.convertMimeMessage(mimeMessage, recipientAddress);
    }
}

