/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.auth;

import org.apache.http.ProtocolException;

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

