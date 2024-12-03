/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.Authorization
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.confluence.oauth2.OAuth2Exception
 *  com.atlassian.confluence.oauth2.OAuth2Service
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  javax.mail.MessagingException
 *  javax.mail.NoSuchProviderException
 *  javax.mail.Session
 *  javax.mail.Store
 *  javax.mail.URLName
 */
package com.atlassian.confluence.internal.diagnostics.ipd.mail.incoming;

import com.atlassian.confluence.internal.diagnostics.ipd.mail.ConnectionVerifier;
import com.atlassian.confluence.mail.Authorization;
import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.oauth2.OAuth2Exception;
import com.atlassian.confluence.oauth2.OAuth2Service;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.naming.NamingException;

public class DefaultIncomingConnectionVerifier
implements ConnectionVerifier {
    private static final long MAX_TIMEOUT = 10000L;
    private final OAuth2Service oAuth2Service;

    public DefaultIncomingConnectionVerifier(OAuth2Service oAuth2Service) {
        this.oAuth2Service = oAuth2Service;
    }

    @Override
    public void verifyConnection(MailServer mailServer) throws Exception {
        try {
            this.doVerify(mailServer);
        }
        catch (Exception ex) {
            throw new MailException("Failed to connect to incoming mail server", (Throwable)ex);
        }
    }

    private void doVerify(MailServer server) throws NamingException, MailException, MessagingException, OAuth2Exception {
        InboundMailServer inboundMailServer = (InboundMailServer)server;
        try (Store store = this.getStore(inboundMailServer);){
            if (!inboundMailServer.isBasicAuth()) {
                ClientTokenEntity token = this.getToken(inboundMailServer);
                store.connect(store.getURLName().getHost(), store.getURLName().getUsername(), token.getAccessToken());
            } else {
                store.connect(server.getHostname(), Integer.parseInt(server.getPort()), server.getUsername(), server.getPassword());
            }
            DefaultIncomingConnectionVerifier.verifyAccess(store);
        }
    }

    private static void verifyAccess(Store store) throws MessagingException {
        store.getFolder("INBOX").open(1);
    }

    private Store getStore(InboundMailServer inboundMailServer) throws NoSuchProviderException, OAuth2Exception, MailException, NamingException {
        Session session = ConnectionVerifier.cloneSessionWithMaxTimeout(inboundMailServer.getSession(), 10000L, this.getAuthenticationType(inboundMailServer));
        if (!inboundMailServer.isBasicAuth()) {
            ClientTokenEntity token = this.getToken(inboundMailServer);
            return session.getStore(new URLName(inboundMailServer.getMailProtocol().getProtocol(), inboundMailServer.getHostname(), Integer.parseInt(inboundMailServer.getPort()), null, inboundMailServer.getUsername(), token.getAccessToken()));
        }
        return session.getStore(inboundMailServer.getMailProtocol().getProtocol());
    }

    private ClientTokenEntity getToken(InboundMailServer mailServer) throws OAuth2Exception {
        Authorization auth = mailServer.getAuthorization();
        if (auth != null) {
            return this.oAuth2Service.getToken(auth.getTokenId());
        }
        return ClientTokenEntity.builder().build();
    }
}

