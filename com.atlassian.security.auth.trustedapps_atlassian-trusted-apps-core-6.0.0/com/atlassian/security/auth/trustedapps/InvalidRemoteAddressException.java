/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.InvalidIPAddressException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;

public class InvalidRemoteAddressException
extends InvalidIPAddressException {
    public InvalidRemoteAddressException(String ipAddress) {
        super(TransportErrorMessage.Code.BAD_REMOTE_IP, ipAddress);
    }
}

