/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.createcontent.exceptions;

import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.AbstractTypedRuntimeException;
import com.atlassian.plugin.ModuleDescriptor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModuleNotBlueprintException
extends AbstractTypedRuntimeException {
    public ModuleNotBlueprintException(@Nonnull String message, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        super(message, errorType, errorData);
    }

    public ModuleNotBlueprintException(@Nonnull String moduleCompleteKey, @Nonnull ModuleDescriptor<?> moduleDescriptor, @Nonnull ResourceErrorType errorType, @Nullable Object errorData) {
        this("Module " + moduleCompleteKey + " is not a BlueprintModuleDescriptor. It is a " + (moduleDescriptor != null ? moduleDescriptor.getClass().getName() : "null"), errorType, errorData);
    }
}

