/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.upm.core.install;

import com.atlassian.sal.api.net.ResponseException;

public class AccessDeniedException
extends ResponseException {
    public AccessDeniedException() {
    }

    public AccessDeniedException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AccessDeniedException(String s) {
        super(s);
    }

    public AccessDeniedException(Throwable throwable) {
        super(throwable);
    }
}

