/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;

public class EmailThreadConversionStartedEvent {
    private final StagedEmailThreadKey key;

    public EmailThreadConversionStartedEvent(StagedEmailThreadKey key) {
        this.key = key;
    }
}

