/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

public class InvalidHashException
extends Exception {
    private static final long serialVersionUID = -8395417336093625256L;
    public static final InvalidHashException INVALID_ACCESS_T0KEN_HASH_EXCEPTION = new InvalidHashException("Access token hash (at_hash) mismatch");
    public static final InvalidHashException INVALID_CODE_HASH_EXCEPTION = new InvalidHashException("Authorization code hash (c_hash) mismatch");
    public static final InvalidHashException INVALID_STATE_HASH_EXCEPTION = new InvalidHashException("State hash (s_hash) mismatch");

    private InvalidHashException(String message) {
        super(message);
    }
}

