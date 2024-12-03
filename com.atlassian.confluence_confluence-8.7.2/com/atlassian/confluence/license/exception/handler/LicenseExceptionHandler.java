/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.license.exception.handler;

public interface LicenseExceptionHandler<E extends Exception> {
    public String handle(E var1);
}

