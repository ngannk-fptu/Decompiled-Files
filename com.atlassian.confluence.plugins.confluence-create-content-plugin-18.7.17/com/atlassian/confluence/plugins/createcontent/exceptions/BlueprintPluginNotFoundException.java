/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.AbstractTypedRuntimeException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlueprintPluginNotFoundException
extends AbstractTypedRuntimeException {
    public BlueprintPluginNotFoundException(@Nonnull String message, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(message, errorType, errorData);
    }
}

