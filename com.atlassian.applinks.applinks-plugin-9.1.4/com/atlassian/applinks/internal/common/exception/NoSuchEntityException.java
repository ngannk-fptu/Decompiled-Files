/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.ServiceException;
import javax.annotation.Nullable;

public abstract class NoSuchEntityException
extends ServiceException {
    protected NoSuchEntityException(@Nullable String message) {
        super(message);
    }

    protected NoSuchEntityException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}

