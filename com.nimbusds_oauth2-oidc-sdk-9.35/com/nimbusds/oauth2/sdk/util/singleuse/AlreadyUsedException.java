/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util.singleuse;

public class AlreadyUsedException
extends Exception {
    private static final long serialVersionUID = -496592623903335352L;

    public AlreadyUsedException(String message) {
        super(message);
    }
}

