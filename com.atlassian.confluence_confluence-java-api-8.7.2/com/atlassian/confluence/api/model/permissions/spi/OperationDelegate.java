/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.model.permissions.spi;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.permissions.spi.OperationCheck;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

@ExperimentalSpi
public interface OperationDelegate {
    public List<OperationCheck> getAllOperations();

    public @Nullable OperationCheck getOperation(OperationKey var1);
}

