/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.exception;

import com.onelogin.saml2.exception.SAMLException;

public class SettingsException
extends SAMLException {
    private static final long serialVersionUID = 1L;
    public static final int SETTINGS_INVALID_SYNTAX = 1;
    public static final int SETTINGS_INVALID = 2;
    public static final int CERT_NOT_FOUND = 3;
    public static final int PRIVATE_KEY_NOT_FOUND = 4;
    public static final int PUBLIC_CERT_FILE_NOT_FOUND = 5;
    public static final int PRIVATE_KEY_FILE_NOT_FOUND = 6;
    private int errorCode;

    public SettingsException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

