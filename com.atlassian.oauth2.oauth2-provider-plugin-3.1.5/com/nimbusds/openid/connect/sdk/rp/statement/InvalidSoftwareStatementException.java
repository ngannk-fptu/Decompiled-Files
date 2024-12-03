/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.rp.statement;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.client.RegistrationError;

public class InvalidSoftwareStatementException
extends Exception {
    private static final long serialVersionUID = -3170931736329757864L;

    public InvalidSoftwareStatementException(String message) {
        this(message, null);
    }

    public InvalidSoftwareStatementException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorObject getErrorObject() {
        if (this.getMessage() != null) {
            return RegistrationError.INVALID_SOFTWARE_STATEMENT.setDescription(this.getMessage());
        }
        return RegistrationError.INVALID_SOFTWARE_STATEMENT;
    }
}

