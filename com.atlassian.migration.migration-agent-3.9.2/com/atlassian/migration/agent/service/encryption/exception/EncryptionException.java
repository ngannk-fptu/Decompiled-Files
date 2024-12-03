/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.encryption.exception;

import lombok.Generated;

public class EncryptionException
extends RuntimeException {
    private final EncryptionErrorCode encryptionErrorCode;

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
        this.encryptionErrorCode = EncryptionErrorCode.INTERNAL_SERVER_ERROR;
    }

    public EncryptionException(String message) {
        super(message);
        this.encryptionErrorCode = EncryptionErrorCode.INTERNAL_SERVER_ERROR;
    }

    public EncryptionException(String message, EncryptionErrorCode encryptionErrorCode) {
        super(message);
        this.encryptionErrorCode = encryptionErrorCode;
    }

    public EncryptionException(String message, Throwable cause, EncryptionErrorCode encryptionErrorCode) {
        super(message, cause);
        this.encryptionErrorCode = encryptionErrorCode;
    }

    @Generated
    public EncryptionErrorCode getEncryptionErrorCode() {
        return this.encryptionErrorCode;
    }

    public static enum EncryptionErrorCode {
        SECRET_KEY_NOT_FOUND,
        INVALID_SECRET_KEY,
        INTERNAL_SERVER_ERROR;

    }
}

