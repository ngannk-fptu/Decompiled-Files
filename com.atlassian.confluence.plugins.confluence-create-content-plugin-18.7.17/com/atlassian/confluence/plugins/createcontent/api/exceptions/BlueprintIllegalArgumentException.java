/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.api.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.AbstractTypedException;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlueprintIllegalArgumentException
extends AbstractTypedException {
    public BlueprintIllegalArgumentException(@Nonnull String message, @Nonnull ResourceErrorType errorType) {
        this(message, errorType, null);
    }

    public BlueprintIllegalArgumentException(@Nonnull String message, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(message, errorType, errorData);
    }
}

