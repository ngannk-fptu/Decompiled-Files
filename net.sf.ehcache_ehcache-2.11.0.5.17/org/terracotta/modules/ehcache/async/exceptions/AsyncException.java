/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async.exceptions;

public class AsyncException
extends Exception {
    public AsyncException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AsyncException(String msg) {
        this(msg, null);
    }
}

