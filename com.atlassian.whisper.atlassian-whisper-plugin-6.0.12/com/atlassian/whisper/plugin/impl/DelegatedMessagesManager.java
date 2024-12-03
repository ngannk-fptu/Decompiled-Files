/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.whisper.plugin.api.HashCalculator
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessagesManager
 *  com.atlassian.whisper.plugin.api.UserMessage
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.whisper.plugin.api.HashCalculator;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessagesManager;
import com.atlassian.whisper.plugin.api.UserMessage;
import com.atlassian.whisper.plugin.impl.DefaultMessagesManager;
import com.atlassian.whisper.plugin.impl.provider.MessagesProviderAccessor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ExportAsService
public class DelegatedMessagesManager
implements MessagesManager {
    private final DefaultMessagesManager delegate;
    private final MessagesProviderAccessor messagesProviderAccessor;

    @Inject
    public DelegatedMessagesManager(@ComponentImport ActiveObjects activeObjects, @ComponentImport UserManager userManager, HashCalculator hashCalculator, MessagesProviderAccessor messagesProviderAccessor) {
        this(messagesProviderAccessor, new DefaultMessagesManager(activeObjects, userManager, hashCalculator));
    }

    @VisibleForTesting
    DelegatedMessagesManager(MessagesProviderAccessor messagesProviderAccessor, DefaultMessagesManager delegate) {
        this.delegate = delegate;
        this.messagesProviderAccessor = messagesProviderAccessor;
    }

    public void addMessage(Message message) {
        this.delegate.addMessage(message);
    }

    public void removeMessage(String messageId) {
        this.delegate.removeMessage(messageId);
    }

    public void addUserMessage(String userHash, String messageId) {
        this.delegate.addUserMessage(userHash, messageId);
    }

    public void addUsersMessages(Set<UserMessage> usersMessages) {
        this.delegate.addUsersMessages(usersMessages);
    }

    public void removeUserMessage(String userHash, String messageId) {
        this.delegate.removeUserMessage(userHash, messageId);
    }

    public void removeUserMessages(String userHash) {
        this.delegate.removeUserMessages(userHash);
    }

    public void removeAllMessagesAndMappings() {
        this.delegate.removeAllMessagesAndMappings();
    }

    public Set<Message> getMessagesForUser(UserProfile user) {
        return Sets.union(this.delegate.getMessagesForUser(user), this.messagesProviderAccessor.getMessagesForUser(user));
    }

    public Set<Message> getMessages() {
        return Sets.union(this.delegate.getMessages(), this.messagesProviderAccessor.getMessages());
    }

    public boolean hasMessages() {
        return this.delegate.hasMessages() || this.messagesProviderAccessor.hasMessages();
    }

    public boolean hasMessages(UserProfile user) {
        return this.delegate.hasMessages(user) || this.messagesProviderAccessor.hasMessages(user);
    }
}

