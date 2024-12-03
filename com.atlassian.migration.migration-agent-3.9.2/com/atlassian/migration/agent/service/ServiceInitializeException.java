/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

public class ServiceInitializeException
extends RuntimeException {
    public ServiceInitializeException(String message) {
        super(message);
    }

    public ServiceInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}

