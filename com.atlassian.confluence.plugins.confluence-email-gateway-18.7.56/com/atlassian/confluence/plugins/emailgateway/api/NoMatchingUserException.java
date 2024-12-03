/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.confluence.plugins.emailgateway.api.EmailHandlingException;

public class NoMatchingUserException
extends EmailHandlingException {
    private final String emailAddress;

    public NoMatchingUserException(String emailAddress) {
        super("No matching user to handling email");
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }
}

