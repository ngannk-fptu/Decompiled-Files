/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.policy.language;

public class PolicyViolationException
extends Exception {
    private static final long serialVersionUID = -1953769818688720916L;

    public PolicyViolationException(String message) {
        super(message);
    }

    public PolicyViolationException(String message, Throwable cause) {
        super(message);
    }
}

