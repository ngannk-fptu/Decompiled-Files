/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.restapi.resources;

import java.util.Optional;

public interface OptionalServiceProvider {
    public Optional<Object> getInvocationHistoryService();

    public Optional<Object> getWebhookService();
}

