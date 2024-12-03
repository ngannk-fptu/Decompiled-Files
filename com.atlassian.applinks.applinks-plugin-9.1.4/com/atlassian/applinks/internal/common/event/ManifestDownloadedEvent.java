/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.common.event;

import com.atlassian.applinks.spi.Manifest;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Objects;
import javax.annotation.Nonnull;

@AsynchronousPreferred
public class ManifestDownloadedEvent {
    private final Manifest manifest;

    public ManifestDownloadedEvent(@Nonnull Manifest manifest) {
        this.manifest = Objects.requireNonNull(manifest, "manifest");
    }

    @Nonnull
    public Manifest getManifest() {
        return this.manifest;
    }
}

