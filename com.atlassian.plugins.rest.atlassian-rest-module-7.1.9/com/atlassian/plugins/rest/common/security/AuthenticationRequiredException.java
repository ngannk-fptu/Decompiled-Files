/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.security;

public class AuthenticationRequiredException
extends SecurityException {
    public AuthenticationRequiredException() {
        super("Client must be authenticated to access this resource.");
    }
}

