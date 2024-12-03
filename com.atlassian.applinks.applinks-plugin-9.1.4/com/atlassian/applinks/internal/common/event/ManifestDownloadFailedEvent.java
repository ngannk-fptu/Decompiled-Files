/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.event;

import com.atlassian.event.api.AsynchronousPreferred;
import java.net.URI;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@AsynchronousPreferred
public class ManifestDownloadFailedEvent {
    private final URI uri;
    private final Throwable cause;

    public ManifestDownloadFailedEvent(@Nonnull URI uri, @Nullable Throwable exception) {
        this.uri = Objects.requireNonNull(uri, "uri");
        this.cause = exception;
    }

    @Nonnull
    public URI getUri() {
        return this.uri;
    }

    @Nullable
    public Throwable getCause() {
        return this.cause;
    }
}

