/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.RestTypedException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractTypedRuntimeException
extends RuntimeException
implements RestTypedException {
    private final ResourceErrorType errorType;
    private final Object errorData;

    public AbstractTypedRuntimeException(@Nonnull Throwable cause, @Nonnull ResourceErrorType errorType) {
        this(cause, errorType, null);
    }

    public AbstractTypedRuntimeException(@Nonnull Throwable cause, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(cause);
        this.errorType = errorType;
        this.errorData = errorData;
    }

    public AbstractTypedRuntimeException(@Nonnull String message, @Nonnull ResourceErrorType errorType) {
        this(message, errorType, null);
    }

    public AbstractTypedRuntimeException(@Nonnull String message, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(message);
        this.errorType = errorType;
        this.errorData = errorData;
    }

    public AbstractTypedRuntimeException(@Nonnull String message, @Nonnull Throwable cause, @Nonnull ResourceErrorType errorType) {
        this(message, cause, errorType, null);
    }

    public AbstractTypedRuntimeException(@Nonnull String message, @Nonnull Throwable cause, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(message, cause);
        this.errorType = errorType;
        this.errorData = errorData;
    }

    @Override
    public ResourceErrorType getErrorType() {
        return this.errorType;
    }

    @Override
    public Object getErrorData() {
        return this.errorData;
    }
}

