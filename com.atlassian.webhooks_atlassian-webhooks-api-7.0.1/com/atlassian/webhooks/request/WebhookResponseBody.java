/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface WebhookResponseBody {
    @Nonnull
    public InputStream getContent() throws IOException;

    @Nonnull
    public Optional<String> getContentType();
}

