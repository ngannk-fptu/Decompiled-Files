/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.diagnostics;

import com.atlassian.webhooks.WebhookEvent;
import javax.annotation.Nonnull;

public enum WebhookDiagnosticsEvent implements WebhookEvent
{
    PING("diagnostics:ping");

    private static final String I18N_PREFIX = "webhooks.diagnostics.event.";
    private final String i18nKey;
    private final String id;

    private WebhookDiagnosticsEvent(String id) {
        this.id = id;
        this.i18nKey = I18N_PREFIX + this.name().toLowerCase();
    }

    @Override
    @Nonnull
    public String getI18nKey() {
        return this.i18nKey;
    }

    @Override
    @Nonnull
    public String getId() {
        return this.id;
    }
}

