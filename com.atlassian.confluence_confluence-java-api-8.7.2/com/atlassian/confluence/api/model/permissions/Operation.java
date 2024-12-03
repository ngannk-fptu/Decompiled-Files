/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.model.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalApi
public interface Operation {
    public @NonNull OperationKey getOperationKey();
}

