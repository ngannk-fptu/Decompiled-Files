/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.oauth2.sdk.ErrorObject;

public final class RegistrationError {
    public static final ErrorObject INVALID_REDIRECT_URI = new ErrorObject("invalid_redirect_uri", "Invalid redirection URI(s)", 400);
    public static final ErrorObject INVALID_CLIENT_METADATA = new ErrorObject("invalid_client_metadata", "Invalid client metadata field", 400);
    public static final ErrorObject INVALID_SOFTWARE_STATEMENT = new ErrorObject("invalid_software_statement", "Invalid software statement", 400);
    public static final ErrorObject UNAPPROVED_SOFTWARE_STATEMENT = new ErrorObject("unapproved_software_statement", "Unapproved software statement", 400);

    private RegistrationError() {
    }
}

