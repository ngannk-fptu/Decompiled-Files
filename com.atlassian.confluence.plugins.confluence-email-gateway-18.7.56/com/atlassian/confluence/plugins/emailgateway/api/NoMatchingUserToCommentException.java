/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserException;

public class NoMatchingUserToCommentException
extends NoMatchingUserException {
    public NoMatchingUserToCommentException(String emailAddress) {
        super(emailAddress);
    }
}

