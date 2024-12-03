/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.InvalidRequestException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;

public class InvalidRequestUrlException
extends InvalidRequestException {
    public InvalidRequestUrlException(String url) {
        super(new TransportErrorMessage(TransportErrorMessage.Code.BAD_URL, "Request not allowed to access URL: {0}", url));
    }
}

