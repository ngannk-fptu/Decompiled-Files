/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.InvalidIPAddressException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;

public class InvalidXForwardedForAddressException
extends InvalidIPAddressException {
    public InvalidXForwardedForAddressException(String ipAddress) {
        super(TransportErrorMessage.Code.BAD_XFORWARD_IP, ipAddress);
    }
}

