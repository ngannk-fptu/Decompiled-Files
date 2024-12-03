/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.mail.MailException
 *  com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException
 *  com.google.common.base.Throwables
 *  javax.mail.AuthenticationFailedException
 *  javax.mail.Folder
 *  javax.mail.MessagingException
 *  javax.mail.Session
 *  javax.mail.Store
 *  javax.mail.URLName
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.mail.MailException;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import com.google.common.base.Throwables;
import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderUtils {
    private static final Logger log = LoggerFactory.getLogger(FolderUtils.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T execute(FolderWorker<T> worker) throws MessagingException, MailException {
        T t;
        Store store = null;
        Folder folder = null;
        try {
            Session session = worker.getMailSession();
            if (worker.getMailServer().isBasicAuth()) {
                store = session.getStore();
                store.connect();
            } else {
                InboundMailServer mailServer = worker.getMailServer();
                store = session.getStore(new URLName(mailServer.getMailProtocol().getProtocol(), null, Integer.parseInt(mailServer.getPort()), null, null, null));
                String accessToken = worker.getAccessToken();
                try {
                    store.connect(mailServer.getHostname(), mailServer.getUsername(), accessToken);
                }
                catch (AuthenticationFailedException ex) {
                    log.error("XOAUTH2 authentication to service {} failed. Trying to recover.", (Object)store.getURLName());
                    throw ex;
                }
            }
            folder = worker.openFolder(store);
            t = worker.doWithFolder(folder);
        }
        catch (Exception e) {
            try {
                Throwables.throwIfInstanceOf((Throwable)e, MailException.class);
                Throwables.throwIfInstanceOf((Throwable)e, MessagingException.class);
                throw new MailException((Throwable)e);
            }
            catch (Throwable throwable) {
                try {
                    FolderUtils.closeQuietlyIfOpen(folder);
                    throw throwable;
                }
                finally {
                    FolderUtils.closeQuietly(store);
                }
            }
        }
        try {
            FolderUtils.closeQuietlyIfOpen(folder);
            return t;
        }
        finally {
            FolderUtils.closeQuietly(store);
        }
    }

    private static void closeQuietly(Store store) {
        if (store != null) {
            try {
                store.close();
            }
            catch (MessagingException e) {
                log.error("Failed to close mail session", (Throwable)e);
            }
        }
    }

    private static void closeQuietlyIfOpen(Folder folder) {
        try {
            if (folder == null) {
                log.debug("Folder is null - this shouldn't be happening!");
            } else if (!folder.isOpen()) {
                log.error("Folder is already closed. This may prevent previously-processed messages from being deleted!");
            } else {
                folder.close(true);
            }
        }
        catch (MessagingException e) {
            log.error("Failed to close mail folder", (Throwable)e);
        }
    }

    public static interface FolderWorker<T> {
        public Folder openFolder(Store var1) throws MessagingException;

        public T doWithFolder(Folder var1) throws MessagingException;

        public Session getMailSession() throws MailException, MessagingException;

        public InboundMailServer getMailServer();

        public String getAccessToken() throws TokenNotFoundException;
    }
}

