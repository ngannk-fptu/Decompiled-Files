/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.api.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.RestTypedException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractTypedException
extends Exception
implements RestTypedException {
    private final ResourceErrorType errorType;
    private final Object errorData;

    public AbstractTypedException(@Nonnull Throwable cause, @Nonnull ResourceErrorType errorType) {
        this(cause, errorType, null);
    }

    public AbstractTypedException(@Nonnull Throwable cause, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(cause);
        this.errorType = errorType;
        this.errorData = errorData;
    }

    public AbstractTypedException(@Nonnull String message, @Nonnull ResourceErrorType errorType) {
        this(message, errorType, null);
    }

    public AbstractTypedException(@Nonnull String message, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(message);
        this.errorType = errorType;
        this.errorData = errorData;
    }

    public AbstractTypedException(@Nonnull String message, @Nonnull Throwable cause, @Nonnull ResourceErrorType errorType) {
        this(message, cause, errorType, null);
    }

    public AbstractTypedException(@Nonnull String message, @Nonnull Throwable cause, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
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

