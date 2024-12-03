/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.internal.PluginDetailsSupplier;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;

public interface PluginHelper
extends PluginDetailsSupplier {
    @Nonnull
    public Optional<Bundle> getCallingBundle();

    @Nonnull
    public String getPluginName(@Nonnull String var1);

    public boolean isUserInstalled(Bundle var1);
}

