/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.applinks.internal.migration;

import com.atlassian.applinks.internal.migration.AuthenticationConfig;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class AuthenticationStatus {
    private final AuthenticationConfig outgoing;
    private final AuthenticationConfig incoming;

    public AuthenticationStatus(@Nonnull AuthenticationConfig incoming, @Nonnull AuthenticationConfig outgoing) {
        this.outgoing = Objects.requireNonNull(outgoing, "outgoing");
        this.incoming = Objects.requireNonNull(incoming, "incoming");
    }

    public AuthenticationConfig outgoing() {
        return this.outgoing;
    }

    public AuthenticationConfig incoming() {
        return this.incoming;
    }

    public AuthenticationStatus outgoing(@Nonnull AuthenticationConfig outgoing) {
        return new AuthenticationStatus(this.incoming, outgoing);
    }

    public AuthenticationStatus incoming(@Nonnull AuthenticationConfig incoming) {
        return new AuthenticationStatus(incoming, this.outgoing);
    }
}

