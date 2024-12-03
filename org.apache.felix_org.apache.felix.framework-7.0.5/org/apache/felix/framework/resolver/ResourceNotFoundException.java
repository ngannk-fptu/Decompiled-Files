/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.resolver;

public class ResourceNotFoundException
extends Exception {
    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException(String msg, Throwable th) {
        super(msg, th);
    }
}

