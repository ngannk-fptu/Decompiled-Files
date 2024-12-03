/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.request;

import java.util.Optional;
import javax.annotation.Nonnull;

public interface QueryParameter {
    @Nonnull
    public String getName();

    @Nonnull
    public Optional<String> getValue();
}

