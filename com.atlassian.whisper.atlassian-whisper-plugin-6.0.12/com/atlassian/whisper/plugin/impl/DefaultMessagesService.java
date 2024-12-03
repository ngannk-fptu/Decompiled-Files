/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.whisper.plugin.api.Message
 *  com.atlassian.whisper.plugin.api.MessagesManager
 *  com.atlassian.whisper.plugin.api.MessagesService
 *  com.atlassian.whisper.plugin.api.WhisperStatusService
 *  com.google.common.collect.Iterators
 *  javax.annotation.Nullable
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.codehaus.jettison.json.JSONArray
 *  org.codehaus.jettison.json.JSONException
 *  org.codehaus.jettison.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.whisper.plugin.api.Message;
import com.atlassian.whisper.plugin.api.MessagesManager;
import com.atlassian.whisper.plugin.api.MessagesService;
import com.atlassian.whisper.plugin.api.WhisperStatusService;
import com.google.common.collect.Iterators;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ExportAsService
public class DefaultMessagesService
implements MessagesService {
    private static final Logger log = LoggerFactory.getLogger(DefaultMessagesService.class);
    private final UserManager userManager;
    private final MessagesManager messagesManager;
    private final WhisperStatusService whisperStatusService;

    @Inject
    public DefaultMessagesService(@ComponentImport UserManager userManager, MessagesManager messagesManager, WhisperStatusService whisperStatusService) {
        this.userManager = userManager;
        this.messagesManager = messagesManager;
        this.whisperStatusService = whisperStatusService;
    }

    public Set<Message> getMessagesForCurrentUser() {
        UserProfile user = this.userManager.getRemoteUser();
        return this.getMessagesForUser(user);
    }

    private Set<Message> getMessagesForUser(UserProfile user) {
        if (user == null) {
            log.debug("Failed to get currently logged in user");
            return Collections.emptySet();
        }
        if (!this.whisperStatusService.isEnabled()) {
            log.debug("Whisper disabled, don't return any messages.");
            return Collections.emptySet();
        }
        return this.messagesManager.getMessagesForUser(user);
    }

    public boolean hasMessagesForCurrentUser() {
        UserProfile user = this.userManager.getRemoteUser();
        if (user == null) {
            log.debug("Failed to get currently logged in user");
            return false;
        }
        return this.messagesManager.hasMessages(user);
    }

    public boolean hasOverride(String experienceId) {
        return this.hasOverride(this.userManager.getRemoteUser(), experienceId, Locale.ENGLISH);
    }

    public boolean hasOverride(UserProfile user, String experienceId) {
        return this.getMessagesForUser(user).stream().anyMatch(message -> this.isOverwritten(experienceId, (Message)message, null));
    }

    public boolean hasOverride(UserProfile user, String experienceId, Locale userLocale) {
        return this.getMessagesForUser(user).stream().anyMatch(message -> this.isOverwritten(experienceId, (Message)message, userLocale));
    }

    public boolean hasGlobalOverride(String experienceId) {
        return this.messagesManager.getMessages().stream().anyMatch(message -> this.isOverwritten(experienceId, (Message)message, null));
    }

    private boolean isOverwritten(String experienceId, Message message, @Nullable Locale userLocale) {
        try {
            JSONObject messageJSONObject = new JSONObject(message.getContent());
            if (userLocale != null && (!userLocale.getLanguage().startsWith("en") || this.getDefaultEnglish(messageJSONObject))) {
                Iterator languageIterator = messageJSONObject.getJSONObject("componentInLanguage").keys();
                String userLocaleStr = userLocale.toString();
                if (!Iterators.any((Iterator)languageIterator, userLocaleStr::startsWith)) {
                    return false;
                }
            }
            JSONArray experienceOverrides = messageJSONObject.getJSONArray("experienceOverrides");
            for (int i = 0; i < experienceOverrides.length(); ++i) {
                if (!experienceId.equals(experienceOverrides.getString(i))) continue;
                return true;
            }
            return false;
        }
        catch (JSONException e) {
            return false;
        }
    }

    private boolean getDefaultEnglish(JSONObject messageJSONObject) throws JSONException {
        return messageJSONObject.has("defaultEnglish") && !messageJSONObject.getBoolean("defaultEnglish");
    }
}

