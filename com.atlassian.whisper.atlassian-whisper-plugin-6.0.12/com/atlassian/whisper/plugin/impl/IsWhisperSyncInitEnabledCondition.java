/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.whisper.plugin.api.MessagesManager
 *  com.atlassian.whisper.plugin.api.MessagesService
 *  com.atlassian.whisper.plugin.api.WhisperStatusService
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.whisper.plugin.api.MessagesManager;
import com.atlassian.whisper.plugin.api.MessagesService;
import com.atlassian.whisper.plugin.api.WhisperStatusService;
import com.atlassian.whisper.plugin.impl.IsWhisperEnabledCondition;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class IsWhisperSyncInitEnabledCondition
extends IsWhisperEnabledCondition {
    private final MessagesService messagesService;

    @Inject
    public IsWhisperSyncInitEnabledCondition(WhisperStatusService whisperStatusService, MessagesManager messagesManager, MessagesService messagesService) {
        super(whisperStatusService, messagesManager);
        this.messagesService = messagesService;
    }

    @Override
    protected boolean isConditionTrue() {
        return super.isConditionTrue() && this.messagesService.hasGlobalOverride("whisper-sync-init");
    }

    @Override
    protected String queryKey() {
        return "whisper-sync-init-enabled";
    }
}

