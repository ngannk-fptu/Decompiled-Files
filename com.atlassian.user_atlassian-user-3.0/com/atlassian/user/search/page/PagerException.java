/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.page;

import com.atlassian.user.EntityException;

public class PagerException
extends EntityException {
    public PagerException() {
    }

    public PagerException(String message) {
        super(message);
    }

    public PagerException(Throwable cause) {
        super(cause);
    }

    public PagerException(String message, Throwable cause) {
        super(message, cause);
    }
}

