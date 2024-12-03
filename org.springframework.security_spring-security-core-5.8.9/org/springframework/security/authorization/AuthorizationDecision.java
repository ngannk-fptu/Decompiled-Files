/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.authorization;

public class AuthorizationDecision {
    private final boolean granted;

    public AuthorizationDecision(boolean granted) {
        this.granted = granted;
    }

    public boolean isGranted() {
        return this.granted;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [granted=" + this.granted + "]";
    }
}

