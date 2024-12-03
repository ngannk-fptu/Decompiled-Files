/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backgroundjob.exception;

public class BackgroundJobServiceNotFound
extends RuntimeException {
    public BackgroundJobServiceNotFound(String serviceName) {
        super("Background job service with name " + serviceName + " was not found");
    }
}

