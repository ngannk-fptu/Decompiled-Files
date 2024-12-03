/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.InvalidRequestException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;

public abstract class InvalidIPAddressException
extends InvalidRequestException {
    public InvalidIPAddressException(TransportErrorMessage.Code code, String address) {
        super(new TransportErrorMessage(code, "Request not allowed from IP address: {0}", address));
    }
}

