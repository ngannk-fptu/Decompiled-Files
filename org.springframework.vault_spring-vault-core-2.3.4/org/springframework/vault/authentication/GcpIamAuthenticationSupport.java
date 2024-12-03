/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.authentication;

import java.time.Clock;
import java.time.Duration;

public abstract class GcpIamAuthenticationSupport {
    private final String path;
    private final String role;
    private final Duration jwtValidity;
    private final Clock clock;

    protected GcpIamAuthenticationSupport(String path, String role, Duration jwtValidity, Clock clock) {
        this.path = path;
        this.role = role;
        this.jwtValidity = jwtValidity;
        this.clock = clock;
    }

    public String getPath() {
        return this.path;
    }

    public String getRole() {
        return this.role;
    }

    public Duration getJwtValidity() {
        return this.jwtValidity;
    }

    public Clock getClock() {
        return this.clock;
    }
}

