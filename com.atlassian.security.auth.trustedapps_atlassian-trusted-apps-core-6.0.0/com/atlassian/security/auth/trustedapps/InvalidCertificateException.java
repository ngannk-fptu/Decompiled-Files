/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.TransportException;

public class InvalidCertificateException
extends TransportException {
    public InvalidCertificateException(TransportErrorMessage error, Exception cause) {
        super(error, cause);
    }

    public InvalidCertificateException(TransportErrorMessage error) {
        super(error);
    }

    public InvalidCertificateException(TransportException exception) {
        super(exception.getTransportErrorMessage(), exception);
    }

    @Override
    public String getMessage() {
        Throwable cause = this.getCause();
        if (cause != null) {
            return cause.getMessage();
        }
        return super.getMessage();
    }
}

