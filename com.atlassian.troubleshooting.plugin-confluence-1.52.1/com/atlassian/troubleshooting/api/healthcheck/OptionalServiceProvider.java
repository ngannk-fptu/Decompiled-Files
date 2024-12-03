/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.util.Optional;
import javax.annotation.Nonnull;

public interface OptionalServiceProvider {
    public Optional<Object> get(@Nonnull String var1);
}

