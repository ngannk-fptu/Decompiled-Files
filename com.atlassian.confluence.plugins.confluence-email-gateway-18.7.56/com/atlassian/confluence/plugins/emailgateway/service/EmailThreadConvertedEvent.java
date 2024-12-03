/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;

public class EmailThreadConvertedEvent {
    private final StagedEmailThread emailThread;
    private final ContentEntityObject content;

    public EmailThreadConvertedEvent(StagedEmailThread emailThread, ContentEntityObject content) {
        this.emailThread = emailThread;
        this.content = content;
    }

    public StagedEmailThread getEmailThread() {
        return this.emailThread;
    }

    public ContentEntityObject getContent() {
        return this.content;
    }
}

