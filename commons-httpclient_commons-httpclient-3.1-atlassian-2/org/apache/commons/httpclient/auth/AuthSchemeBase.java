/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.auth;

import org.apache.commons.httpclient.auth.AuthScheme;
import org.apache.commons.httpclient.auth.MalformedChallengeException;

public abstract class AuthSchemeBase
implements AuthScheme {
    private String challenge = null;

    public AuthSchemeBase(String challenge) throws MalformedChallengeException {
        if (challenge == null) {
            throw new IllegalArgumentException("Challenge may not be null");
        }
        this.challenge = challenge;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AuthSchemeBase) {
            return this.challenge.equals(((AuthSchemeBase)obj).challenge);
        }
        return super.equals(obj);
    }

    public int hashCode() {
        return this.challenge.hashCode();
    }

    public String toString() {
        return this.challenge;
    }
}

