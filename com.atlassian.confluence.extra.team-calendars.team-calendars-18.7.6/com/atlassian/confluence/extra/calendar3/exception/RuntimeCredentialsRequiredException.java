/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.CredentialsRequiredException
 */
package com.atlassian.confluence.extra.calendar3.exception;

import com.atlassian.applinks.api.CredentialsRequiredException;

public class RuntimeCredentialsRequiredException
extends RuntimeException {
    private CredentialsRequiredException credentialsRequiredException;

    public RuntimeCredentialsRequiredException(CredentialsRequiredException credentialsRequiredException) {
        this.credentialsRequiredException = credentialsRequiredException;
    }

    public CredentialsRequiredException getCredentialsRequiredException() {
        return this.credentialsRequiredException;
    }
}

