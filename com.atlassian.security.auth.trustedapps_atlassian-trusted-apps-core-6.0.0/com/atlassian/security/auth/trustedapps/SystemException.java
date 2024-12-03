/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;

public class SystemException
extends InvalidCertificateException {
    public SystemException(String appId, Exception cause) {
        super(new TransportErrorMessage.System(cause, appId), cause);
    }
}

