/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.plugin;

import java.util.Optional;
import javax.annotation.Nonnull;

public interface BundleFinder {
    public Optional<String> getBundleNameForClass(@Nonnull Class<?> var1);
}

