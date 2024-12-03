/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.core.spi.service;

import java.util.Optional;
import javax.annotation.Nonnull;

public interface ClusterNodeProvider {
    @Nonnull
    public Optional<String> currentNodeId();
}

