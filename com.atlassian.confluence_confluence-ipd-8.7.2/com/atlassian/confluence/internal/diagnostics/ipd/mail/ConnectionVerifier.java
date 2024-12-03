/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.mail.server.MailServer
 *  javax.mail.Session
 */
package com.atlassian.confluence.internal.diagnostics.ipd.mail;

import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.mail.server.MailServer;
import java.util.Properties;
import javax.mail.Session;

public interface ConnectionVerifier {
    public void verifyConnection(MailServer var1) throws Exception;

    public static Session cloneSessionWithMaxTimeout(Session session, long maxTimeout, AuthenticationType type) {
        Properties props = (Properties)session.getProperties().clone();
        if (type == AuthenticationType.OAUTH2) {
            props.setProperty("mail.pop3s.auth.xoauth2.two.line.authentication.format", "true");
            props.setProperty("mail.pop3s.ssl.enable", "true");
            props.setProperty("mail.imap.ssl.enable", "true");
            props.setProperty("mail.pop3s.auth.mechanisms", "XOAUTH2");
            props.setProperty("mail.imaps.auth.mechanisms", "XOAUTH2");
        }
        String protocol = props.getProperty("mail.transport.protocol");
        String connectionTimeout = String.format("mail.%s.connectiontimeout", protocol);
        String socketTimeout = String.format("mail.%s.timeout", protocol);
        ConnectionVerifier.optionallyOverrideTimeout(props, connectionTimeout, maxTimeout);
        ConnectionVerifier.optionallyOverrideTimeout(props, socketTimeout, maxTimeout);
        return Session.getInstance((Properties)props);
    }

    public static Session cloneSessionWithMaxTimeout(Session session, long maxTimeout) {
        return ConnectionVerifier.cloneSessionWithMaxTimeout(session, maxTimeout, AuthenticationType.BASIC);
    }

    public static void optionallyOverrideTimeout(Properties properties, String propertyName, long maxTimeout) {
        String property = properties.getProperty(propertyName);
        if (property == null) {
            properties.setProperty(propertyName, String.valueOf(maxTimeout));
            return;
        }
        try {
            long currentTimeout = Long.parseLong(property);
            if (currentTimeout > maxTimeout) {
                properties.setProperty(propertyName, String.valueOf(maxTimeout));
            }
        }
        catch (NumberFormatException e) {
            properties.setProperty(propertyName, String.valueOf(maxTimeout));
        }
    }

    default public AuthenticationType getAuthenticationType(InboundMailServer mailServer) {
        return mailServer.isBasicAuth() ? AuthenticationType.BASIC : AuthenticationType.OAUTH2;
    }

    public static enum AuthenticationType {
        BASIC,
        OAUTH2;

    }
}

