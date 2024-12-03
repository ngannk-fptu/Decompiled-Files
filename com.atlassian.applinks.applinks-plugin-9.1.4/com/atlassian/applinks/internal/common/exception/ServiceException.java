/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import javax.annotation.Nullable;

public abstract class ServiceException
extends Exception {
    protected ServiceException(@Nullable String message) {
        super(message);
    }

    protected ServiceException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

