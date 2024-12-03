/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.osgi;

public class OsgiNoServiceAvailableException
extends RuntimeException {
    public OsgiNoServiceAvailableException() {
    }

    public OsgiNoServiceAvailableException(String className) {
        super("No service available for " + className);
    }
}

