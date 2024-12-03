/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.security;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import java.util.Objects;

public class ForgotPasswordUnknownUserEvent
extends ConfluenceEvent {
    private static final long serialVersionUID = -6101549753371154970L;
    private final String usernameOrEmail;

    public ForgotPasswordUnknownUserEvent(Object src, String usernameOrEmail) {
        super(src);
        this.usernameOrEmail = Objects.requireNonNull(usernameOrEmail);
    }

    public String getUsernameOrEmail() {
        return this.usernameOrEmail;
    }
}

