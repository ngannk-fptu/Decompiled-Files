/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.concurrent.Lock
 *  com.atlassian.confluence.core.ConfluenceException
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.ClientTokenMetadata$ClientTokenStatus
 *  com.atlassian.oauth2.client.api.storage.TokenHandler
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService
 *  com.atlassian.oauth2.client.api.storage.token.exception.RecoverableTokenException
 *  com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException
 *  com.google.common.collect.ImmutableMap
 *  com.sun.mail.pop3.POP3Message
 *  javax.mail.AuthenticationFailedException
 *  javax.mail.Flags$Flag
 *  javax.mail.Folder
 *  javax.mail.Message
 *  javax.mail.MessagingException
 *  javax.mail.NoSuchProviderException
 *  javax.mail.Session
 *  javax.mail.Store
 *  javax.mail.URLName
 *  javax.mail.internet.MimeMessage
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.concurrent.Lock;
import com.atlassian.confluence.core.ConfluenceException;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.mail.archive.MailAccount;
import com.atlassian.confluence.mail.archive.MailAccountManager;
import com.atlassian.confluence.mail.archive.MailContentManager;
import com.atlassian.confluence.mail.archive.MailPollResult;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.ClientTokenMetadata;
import com.atlassian.oauth2.client.api.storage.TokenHandler;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenStorageService;
import com.atlassian.oauth2.client.api.storage.token.exception.RecoverableTokenException;
import com.atlassian.oauth2.client.api.storage.token.exception.UnrecoverableTokenException;
import com.google.common.collect.ImmutableMap;
import com.sun.mail.pop3.POP3Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMailAccountManager
implements MailAccountManager {
    private static final String MAIL_POLLING_DISABLED_PROPERTY = "atlassian.mail.fetchdisabled";
    private static final Logger log = LoggerFactory.getLogger(DefaultMailAccountManager.class);
    private static final String MAIL_TIMEOUT_MILLIS = "60000";
    static final Map<String, String> OAUTH2_PROPERTIES = ImmutableMap.of((Object)"mail.imap.ssl.enable", (Object)"true", (Object)"mail.pop3s.ssl.enable", (Object)"true", (Object)"mail.imaps.auth.mechanisms", (Object)"XOAUTH2", (Object)"mail.pop3s.auth.mechanisms", (Object)"XOAUTH2");
    private final ClusterManager clusterManager;
    private final BandanaManager bandanaManager;
    private final MailContentManager mailContentManager;
    private final SpaceManager spaceManager;
    private final ClientTokenStorageService clientTokenStorageService;
    private final TokenHandler tokenHandler;

    public DefaultMailAccountManager(ClusterManager clusterManager, BandanaManager bandanaManager, MailContentManager mailContentManager, SpaceManager spaceManager, ClientTokenStorageService clientTokenStorageService, TokenHandler tokenHandler) {
        this.clusterManager = clusterManager;
        this.bandanaManager = bandanaManager;
        this.mailContentManager = mailContentManager;
        this.spaceManager = spaceManager;
        this.clientTokenStorageService = clientTokenStorageService;
        this.tokenHandler = tokenHandler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MailPollResult updateAccountStatus(MailAccount mailAccount) {
        Store store = null;
        String mailAccountNameAndDescription = mailAccount.getName() + " (" + mailAccount.getDescription() + ")";
        try {
            store = this.getStore(mailAccount);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e.getCause());
            MailPollResult mailPollResult = MailPollResult.failure(mailAccountNameAndDescription, e.getMessage());
            return mailPollResult;
        }
        finally {
            this.closeStore(store);
        }
        return MailPollResult.success(mailAccountNameAndDescription, 0);
    }

    @Override
    public List<MailPollResult> poll(Space space) {
        List<MailAccount> accounts = this.getMailAccounts(space);
        ArrayList<MailPollResult> results = new ArrayList<MailPollResult>(accounts.size());
        for (MailAccount mailAccount : accounts) {
            log.info("Checking for new mail in account " + mailAccount.getName() + " for space " + space.getKey());
            if (mailAccount.isEnabled()) {
                MailPollResult result = this.poll(space, mailAccount);
                log.info("New mail check complete for account " + mailAccount.getName() + " in space " + space.getKey() + ": " + result);
                results.add(result);
                continue;
            }
            log.info("Account " + mailAccount.getName() + " in space " + space.getKey() + " is disabled");
        }
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MailPollResult poll(Space space, MailAccount mailAccount) {
        if (Boolean.getBoolean(MAIL_POLLING_DISABLED_PROPERTY)) {
            log.info("Mail polling is disabled via system property.");
            return MailPollResult.success("Mail polling is disabled via system property.", 0);
        }
        String mailAccountNameAndDescription = mailAccount.getName() + " (" + mailAccount.getDescription() + ")";
        Lock lock = this.clusterManager.getLock(mailAccount.lockName());
        if (!lock.tryLock()) {
            return MailPollResult.failure(mailAccountNameAndDescription, "Account is already being polled");
        }
        try {
            if (!mailAccount.isEnabled()) {
                MailPollResult mailPollResult = MailPollResult.failure(mailAccountNameAndDescription, "Account is not enabled");
                return mailPollResult;
            }
            MailPollResult mailPollResult = this.retrieveMessages(space, mailAccount);
            return mailPollResult;
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MailPollResult retrieveMessages(Space space, MailAccount mailAccount) throws MessagingException {
        Store store = null;
        Folder folder = null;
        try {
            store = this.getStore(mailAccount);
        }
        catch (Exception e) {
            log.error("Error connecting to " + mailAccount + " for space " + space.getKey() + ": " + e.getMessage(), e.getCause());
            MailPollResult mailPollResult = MailPollResult.failure(mailAccount.getName() + " (" + mailAccount.getDescription() + ")", e.getMessage());
            this.closeFolder(folder);
            this.closeStore(store);
            return mailPollResult;
        }
        try {
            folder = store.getFolder(mailAccount.getFolderName());
            folder.open(2);
            Message[] messages = folder.getMessages();
            int newMsgs = messages.length;
            log.debug("There are {} messages in the INBOX for Pop Account: {}", (Object)newMsgs, (Object)mailAccount.getName());
            for (Message message : messages) {
                MimeMessage msg = (MimeMessage)message;
                MimeMessage msgCopy = new MimeMessage(msg);
                if (msg instanceof POP3Message) {
                    ((POP3Message)msg).invalidate(true);
                }
                try {
                    this.mailContentManager.storeIncomingMail(space, msgCopy);
                }
                catch (ConfluenceException e) {
                    log.warn("Could not store message within Confluence: [" + msgCopy + "] - this message will be left on the server", (Throwable)e);
                    continue;
                }
                this.deleteMessageFromServer(msg);
            }
            MailPollResult mailPollResult = MailPollResult.success(mailAccount.getName() + " (" + mailAccount.getDescription() + ")", newMsgs);
            this.closeFolder(folder);
            this.closeStore(store);
            return mailPollResult;
        }
        catch (Throwable throwable) {
            this.closeFolder(folder);
            this.closeStore(store);
            throw throwable;
        }
    }

    @Override
    public List<MailAccount> getMailAccounts(Space space) {
        List accounts = (List)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(space), "atlassian.confluence.space.mailaccounts", false);
        if (accounts == null) {
            return new ArrayList<MailAccount>();
        }
        return new ArrayList<MailAccount>(accounts);
    }

    @Override
    public MailAccount addMailAccount(Space space, MailAccount mailAccount) {
        List<MailAccount> mailAccounts = this.getMailAccounts(space);
        int id = 1;
        for (MailAccount account : mailAccounts) {
            if (account.getId() < id) continue;
            id = account.getId() + 1;
        }
        mailAccount.setId(id);
        mailAccounts.add(mailAccount);
        this.persistAccounts(space, mailAccounts);
        return mailAccount;
    }

    @Override
    public void removeMailAccount(Space space, int accountId) {
        List<MailAccount> mailAccounts = this.getMailAccounts(space);
        this.removeAccountFromList(mailAccounts, accountId);
        this.persistAccounts(space, mailAccounts);
    }

    @Override
    public MailAccount getMailAccount(Space space, int accountId) {
        for (MailAccount account : this.getMailAccounts(space)) {
            if (account.getId() != accountId) continue;
            return account;
        }
        return null;
    }

    private void persistAccounts(Space space, List newMailAccounts) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(space), "atlassian.confluence.space.mailaccounts", (Object)newMailAccounts);
    }

    @Override
    public void updateAccount(Space space, MailAccount mailAccount) {
        List<MailAccount> mailAccounts = this.getMailAccounts(space);
        int id = mailAccount.getId();
        this.removeAccountFromList(mailAccounts, id);
        mailAccounts.add(mailAccount);
        this.persistAccounts(space, mailAccounts);
    }

    private void removeAccountFromList(List<MailAccount> mailAccounts, int id) {
        Iterator<MailAccount> iterator = mailAccounts.iterator();
        while (iterator.hasNext()) {
            MailAccount account = iterator.next();
            if (account.getId() != id) continue;
            iterator.remove();
            break;
        }
    }

    @Override
    public List<MailPollResult> pollAllSpaces() {
        ArrayList<MailPollResult> results = new ArrayList<MailPollResult>();
        ListBuilder listBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceType(SpaceType.GLOBAL).build());
        for (List spaces : listBuilder) {
            for (Space space : spaces) {
                results.addAll(this.poll(space));
            }
        }
        return results;
    }

    private Store getStore(MailAccount mailAccount) {
        try {
            Properties props = new Properties();
            props.setProperty("mail.imap.timeout", MAIL_TIMEOUT_MILLIS);
            props.setProperty("mail.imap.connectiontimeout", MAIL_TIMEOUT_MILLIS);
            props.setProperty("mail.pop3.timeout", MAIL_TIMEOUT_MILLIS);
            props.setProperty("mail.pop3.connectiontimeout", MAIL_TIMEOUT_MILLIS);
            boolean isBasicAuthentication = this.isBasicAuthentication(mailAccount);
            if (!isBasicAuthentication) {
                OAUTH2_PROPERTIES.forEach(props::setProperty);
            }
            Session session = Session.getInstance((Properties)props, null);
            Store store = null;
            if (isBasicAuthentication) {
                store = session.getStore(mailAccount.getProtocol());
                store.connect(mailAccount.getHostname(), mailAccount.getPort(), mailAccount.getUsername(), mailAccount.getPassword());
            } else {
                store = session.getStore(new URLName(mailAccount.getProtocol(), null, mailAccount.getPort(), null, null, null));
                ClientTokenEntity tokenEntity = this.clientTokenStorageService.getByIdOrFail(mailAccount.getToken());
                try {
                    store.connect(mailAccount.getHostname(), mailAccount.getUsername(), tokenEntity.getAccessToken());
                }
                catch (AuthenticationFailedException ex) {
                    log.debug("XOAUTH2 authentication to service {} failed. Trying to recover.", (Object)store.getURLName());
                    this.recoverOrRethrow(store, mailAccount.getUsername(), tokenEntity, ex);
                }
            }
            mailAccount.setStatus(store.isConnected());
            if (mailAccount.getStatus()) {
                return store;
            }
            throw new InfrastructureException("Unknown error connecting to mail account: " + mailAccount);
        }
        catch (NoSuchProviderException e) {
            throw new InfrastructureException("Configuration error: Javamail could not find provider", (Throwable)e);
        }
        catch (Throwable t) {
            throw new InfrastructureException("Error connecting to mail server: " + t.getMessage(), t);
        }
    }

    private void recoverOrRethrow(Store store, String username, ClientTokenEntity tokenEntity, AuthenticationFailedException originalEx) throws MessagingException {
        Optional<String> accessTokenOpt;
        if (this.isTokenRecoverable(tokenEntity) && (accessTokenOpt = this.refreshAccessToken(tokenEntity.getId())).isPresent()) {
            try {
                store.connect(username, tokenEntity.getAccessToken());
            }
            catch (MessagingException ex) {
                log.error("Issue connecting to store: [{}]", (Object)ex.getMessage(), (Object)ex);
            }
        }
        log.warn("Unable to refresh token: {}", (Object)tokenEntity.getId());
        throw originalEx;
    }

    private Optional<String> refreshAccessToken(String tokenId) {
        try {
            ClientToken clientToken = this.tokenHandler.getRefreshedToken(tokenId);
            return Optional.ofNullable(clientToken).map(ClientToken::getAccessToken);
        }
        catch (RecoverableTokenException | UnrecoverableTokenException ex) {
            log.debug("Access token can not be refreshed.", ex);
            return Optional.empty();
        }
    }

    private boolean isTokenRecoverable(ClientTokenEntity tokenEntity) {
        return !ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE.equals((Object)tokenEntity.getStatus());
    }

    private boolean isBasicAuthentication(MailAccount mailAccount) {
        return mailAccount.getAuthentication() == null || "BasicAuthentication".equals(mailAccount.getAuthentication());
    }

    private void deleteMessageFromServer(MimeMessage msg) throws MessagingException {
        try {
            msg.setFlag(Flags.Flag.DELETED, true);
        }
        catch (MessagingException e) {
            log.error("Could not delete email with messageId [" + msg.getMessageID() + "] from " + this + "\nPlease delete this message manually, as too many undeleteable messages will slow down Confluence");
        }
    }

    private void closeFolder(Folder folder) {
        try {
            if (folder != null) {
                folder.close(true);
            }
        }
        catch (Exception e) {
            log.error("Error closing folder", (Throwable)e);
        }
    }

    private void closeStore(Store store) {
        try {
            if (store != null) {
                store.close();
            }
        }
        catch (Exception e) {
            log.error("Error closing store", (Throwable)e);
        }
    }
}

