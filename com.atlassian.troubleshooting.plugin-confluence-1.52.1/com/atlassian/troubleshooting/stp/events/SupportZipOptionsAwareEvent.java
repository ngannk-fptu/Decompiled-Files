/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.events;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class SupportZipOptionsAwareEvent {
    private final Collection<String> supportZipOptions;

    protected SupportZipOptionsAwareEvent(@Nonnull Collection<String> supportZipOptions) {
        this.supportZipOptions = Objects.requireNonNull(supportZipOptions);
    }

    public Collection<String> getSupportZipOptions() {
        return this.supportZipOptions;
    }
}

