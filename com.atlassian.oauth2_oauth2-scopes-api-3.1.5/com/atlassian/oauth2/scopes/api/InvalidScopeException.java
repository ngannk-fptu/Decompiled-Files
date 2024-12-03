/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.scopes.api;

import java.util.Collection;

public class InvalidScopeException
extends RuntimeException {
    public static InvalidScopeException blankScope() {
        return new InvalidScopeException();
    }

    private InvalidScopeException() {
        super("Blank scope");
    }

    public InvalidScopeException(Collection<String> invalidScopes) {
        super(InvalidScopeException.message(invalidScopes));
    }

    public InvalidScopeException(Collection<String> invalidScopes, Throwable cause) {
        super(InvalidScopeException.message(invalidScopes), cause);
    }

    private static String message(Collection<String> invalidScopes) {
        return String.format("Found invalid scopes [%s]", invalidScopes.toString());
    }
}

