/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.condition.SimpleUrlReadingCondition
 *  com.atlassian.whisper.plugin.api.MessagesManager
 *  com.atlassian.whisper.plugin.api.WhisperStatusService
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.plugin.webresource.condition.SimpleUrlReadingCondition;
import com.atlassian.whisper.plugin.api.MessagesManager;
import com.atlassian.whisper.plugin.api.WhisperStatusService;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class IsWhisperEnabledCondition
extends SimpleUrlReadingCondition {
    private final WhisperStatusService whisperStatusService;
    private final MessagesManager messagesMenager;

    @Inject
    public IsWhisperEnabledCondition(WhisperStatusService whisperStatusService, MessagesManager messagesManager) {
        this.whisperStatusService = whisperStatusService;
        this.messagesMenager = messagesManager;
    }

    protected boolean isConditionTrue() {
        return this.whisperStatusService.isEnabled() && this.messagesMenager.hasMessages();
    }

    protected String queryKey() {
        return "whisper-enabled";
    }
}

