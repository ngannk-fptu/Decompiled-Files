/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.concurrent;

import java.security.SecureRandom;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class ManagedFutureTask<T>
extends FutureTask<T> {
    private static final String ID_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ID_CHARS_LENGTH = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length();
    private static final int ID_LENGTH = 32;
    private static final SecureRandom random = new SecureRandom();
    private String id;

    public ManagedFutureTask(String id, Callable<T> callable) {
        super(callable);
        this.id = id;
    }

    public ManagedFutureTask(String id, Runnable runnable, T result) {
        super(runnable, result);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}

