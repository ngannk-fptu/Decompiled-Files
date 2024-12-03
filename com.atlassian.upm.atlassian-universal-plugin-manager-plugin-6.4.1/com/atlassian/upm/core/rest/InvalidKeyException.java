/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest;

public class InvalidKeyException
extends RuntimeException {
    public InvalidKeyException(String key) {
        super("Cannot unescape key: " + key);
    }
}

