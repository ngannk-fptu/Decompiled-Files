/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.spring.container;

public class ComponentNotFoundException
extends RuntimeException {
    public ComponentNotFoundException(String message) {
        super(message);
    }

    public ComponentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

