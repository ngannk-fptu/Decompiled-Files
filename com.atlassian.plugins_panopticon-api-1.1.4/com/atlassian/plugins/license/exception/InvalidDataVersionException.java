/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.license.exception;

public class InvalidDataVersionException
extends Exception {
    private Long invalidVersion;

    public InvalidDataVersionException(String message, Long invalidVersion) {
        super(message);
        this.invalidVersion = invalidVersion;
    }
}

