/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.whisper.plugin.api.HashCalculator
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessagesManager
 *  com.atlassian.whisper.plugin.api.UserMessage
 *  com.google.common.collect.ImmutableSet
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.whisper.plugin.ao.MessageAO;
import com.atlassian.whisper.plugin.ao.MessageMappingAO;
import com.atlassian.whisper.plugin.api.HashCalculator;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessagesManager;
import com.atlassian.whisper.plugin.api.UserMessage;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMessagesManager
implements MessagesManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultMessagesManager.class);
    private final ActiveObjects activeObjects;
    private final UserManager userManager;
    private final HashCalculator hashCalculator;

    DefaultMessagesManager(ActiveObjects activeObjects, UserManager userManager, HashCalculator hashCalculator) {
        this.activeObjects = activeObjects;
        this.userManager = userManager;
        this.hashCalculator = hashCalculator;
    }

    public Set<Message> getMessagesForUser(UserProfile user) {
        log.debug("Getting messages for user {}", (Object)user.getUsername());
        Query messagesSelectQuery = this.queryForUser(this.queryMessages(), user);
        ImmutableSet<Message> messages = this.getMessages(messagesSelectQuery);
        log.debug("Found {} message(s) for user {}", (Object)messages.size(), (Object)user.getUsername());
        return messages;
    }

    public Set<Message> getMessages() {
        log.debug("Getting all messages");
        Query messagesSelectQuery = this.queryMessages();
        ImmutableSet<Message> messages = this.getMessages(messagesSelectQuery);
        log.debug("Found {} message(s)", (Object)messages.size());
        return messages;
    }

    private ImmutableSet<Message> getMessages(Query messagesSelectQuery) {
        return (ImmutableSet)this.activeObjects.executeInTransaction(() -> {
            HashMap messages = new HashMap();
            this.activeObjects.stream(MessageAO.class, messagesSelectQuery, messageAO -> messages.put(messageAO.getId(), new Message(messageAO.getId(), messageAO.getContent())));
            return ImmutableSet.copyOf(messages.values());
        });
    }

    public void addMessage(Message message) {
        log.debug("Adding message {}", (Object)message.getId());
        this.activeObjects.executeInTransaction(() -> (MessageAO)this.activeObjects.create(MessageAO.class, new DBParam[]{new DBParam("ID", (Object)message.getId()), new DBParam("CONTENT", (Object)message.getContent())}));
    }

    public void removeMessage(String messageId) {
        log.debug("Removing message {}", (Object)messageId);
        this.activeObjects.executeInTransaction(() -> {
            this.activeObjects.deleteWithSQL(MessageMappingAO.class, "MESSAGE_ID = ?", new Object[]{messageId});
            this.activeObjects.deleteWithSQL(MessageAO.class, "ID = ?", new Object[]{messageId});
            return null;
        });
    }

    public void addUserMessage(String userHash, String messageId) {
        this.addUsersMessages((Set<UserMessage>)ImmutableSet.of((Object)new UserMessage(userHash, messageId)));
    }

    public void addUsersMessages(Set<UserMessage> usersMessages) {
        if (log.isTraceEnabled()) {
            log.debug("Adding {} mapping(s)", (Object)usersMessages.size());
        }
        this.activeObjects.executeInTransaction(() -> {
            for (UserMessage mapping : usersMessages) {
                MessageAO messageAO = (MessageAO)this.activeObjects.get(MessageAO.class, (Object)mapping.getMessageId());
                if (messageAO != null) {
                    this.activeObjects.create(MessageMappingAO.class, new DBParam[]{new DBParam("USER_HASH", (Object)mapping.getUserHash()), new DBParam("MESSAGE_ID", (Object)mapping.getMessageId())});
                    continue;
                }
                log.debug("Message {} not found", (Object)mapping.getMessageId());
            }
            return null;
        });
    }

    public void removeUserMessage(String userHash, String messageId) {
        log.debug("Removing user (hash = {}) mapping with message {}", (Object)userHash, (Object)messageId);
        this.activeObjects.executeInTransaction(() -> this.activeObjects.deleteWithSQL(MessageMappingAO.class, "USER_HASH = ? AND MESSAGE_ID = ?", new Object[]{userHash, messageId}));
    }

    public void removeUserMessages(String userHash) {
        log.debug("Removing all user (hash = {}) mappings", (Object)userHash);
        this.activeObjects.executeInTransaction(() -> this.activeObjects.deleteWithSQL(MessageMappingAO.class, "USER_HASH = ?", new Object[]{userHash}));
    }

    public void removeAllMessagesAndMappings() {
        log.debug("Removing all messages and mappings");
        this.activeObjects.executeInTransaction(() -> {
            this.activeObjects.deleteWithSQL(MessageMappingAO.class, null, new Object[0]);
            this.activeObjects.deleteWithSQL(MessageAO.class, null, new Object[0]);
            return null;
        });
    }

    public boolean hasMessages() {
        return (Boolean)this.activeObjects.executeInTransaction(() -> {
            Query messagesSelectQuery = Query.select((String)"ID").limit(1);
            return ((MessageAO[])this.activeObjects.find(MessageAO.class, messagesSelectQuery)).length > 0;
        });
    }

    public boolean hasMessages(UserProfile user) {
        log.debug("Checking if there are messages for user {}", (Object)user.getUsername());
        return (Boolean)this.activeObjects.executeInTransaction(() -> {
            Query messagesSelectQuery = this.queryForUser(Query.select((String)"USER_HASH, MESSAGE_ID"), user).limit(1);
            return ((MessageMappingAO[])this.activeObjects.find(MessageMappingAO.class, messagesSelectQuery)).length > 0;
        });
    }

    private Query queryMessages() {
        return Query.select((String)"ID, CONTENT").alias(MessageAO.class, "message").alias(MessageMappingAO.class, "message_mapping").join(MessageMappingAO.class, "message.ID = message_mapping.MESSAGE_ID");
    }

    private Query queryForUser(Query query, UserProfile user) {
        ArrayList<String> userHashValues = new ArrayList<String>();
        userHashValues.add(this.hashCalculator.calculateUserHash(user.getUsername()));
        userHashValues.add("*");
        if (this.userManager.isSystemAdmin(user.getUserKey())) {
            userHashValues.add("role:sysadmin");
            userHashValues.add("role:admin");
        } else if (this.userManager.isAdmin(user.getUserKey())) {
            userHashValues.add("role:admin");
        } else {
            userHashValues.add("role:user");
        }
        log.debug("Preparing the query for user messages for user {} (hash values = {})", (Object)user.getUsername(), userHashValues);
        String whereClause = String.join((CharSequence)" OR ", Collections.nCopies(userHashValues.size(), "USER_HASH = ?"));
        return query.where(whereClause, userHashValues.toArray());
    }
}

