/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.confluence.plugins.emailgateway.api.NoMatchingUserException;

public class NoMatchingUserToCreatePageException
extends NoMatchingUserException {
    public NoMatchingUserToCreatePageException(String emailAddress) {
        super(emailAddress);
    }
}

