/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ProtocolException
 */
package org.apache.hc.client5.http.auth;

import org.apache.hc.core5.http.ProtocolException;

public class MalformedChallengeException
extends ProtocolException {
    private static final long serialVersionUID = 814586927989932284L;

    public MalformedChallengeException() {
    }

    public MalformedChallengeException(String message) {
        super(message);
    }

    public MalformedChallengeException(String message, Throwable cause) {
        super(message, cause);
    }
}

