/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

class IllegalArgumentExceptionMessages {
    static final String AUTHORITY_URI_EMPTY_PATH_SEGMENT = "Authority Uri should not have empty path segments";
    static final String AUTHORITY_URI_MISSING_PATH_SEGMENT = "Authority Uri must have at least one path segment. This is usually 'common' or the application's tenant id.";
    static final String AUTHORITY_URI_EMPTY_PATH = "Authority Uri should have at least one segment in the path";

    IllegalArgumentExceptionMessages() {
    }
}

