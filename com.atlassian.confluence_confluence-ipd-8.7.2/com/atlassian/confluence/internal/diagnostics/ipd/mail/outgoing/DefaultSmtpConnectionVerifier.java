/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.auth.AuthenticationContextAware
 *  javax.mail.MessagingException
 *  javax.mail.Service
 *  javax.mail.Session
 *  javax.mail.Transport
 */
package com.atlassian.confluence.internal.diagnostics.ipd.mail.outgoing;

import com.atlassian.confluence.internal.diagnostics.ipd.mail.ConnectionVerifier;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.auth.AuthenticationContextAware;
import javax.mail.MessagingException;
import javax.mail.Service;
import javax.mail.Session;
import javax.mail.Transport;
import javax.naming.NamingException;

class DefaultSmtpConnectionVerifier
implements ConnectionVerifier {
    DefaultSmtpConnectionVerifier() {
    }

    @Override
    public void verifyConnection(MailServer mailServer) throws NamingException, MailException, MessagingException {
        Session sessionWithTimeout = ConnectionVerifier.cloneSessionWithMaxTimeout(mailServer.getSession(), 10000L);
        try (Transport transport = sessionWithTimeout.getTransport();){
            if (mailServer instanceof AuthenticationContextAware) {
                ((AuthenticationContextAware)mailServer).getAuthenticationContext().connectService((Service)transport);
            } else {
                transport.connect();
            }
            if (!transport.isConnected()) {
                throw new MailException("Failed to connect to SMTP server");
            }
        }
    }
}

