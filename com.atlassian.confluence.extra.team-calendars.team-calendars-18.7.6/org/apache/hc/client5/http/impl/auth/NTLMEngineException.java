/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl.auth;

import org.apache.hc.client5.http.auth.AuthenticationException;

public class NTLMEngineException
extends AuthenticationException {
    private static final long serialVersionUID = 6027981323731768824L;

    public NTLMEngineException() {
    }

    public NTLMEngineException(String message) {
        super(message);
    }

    public NTLMEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}

