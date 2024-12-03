/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugins.navlink.consumer.menu.services;

import com.atlassian.plugins.capabilities.api.LinkedApplicationCapabilities;
import com.atlassian.plugins.navlink.producer.capabilities.CapabilityKey;
import com.atlassian.plugins.navlink.producer.capabilities.RemoteApplicationWithCapabilities;
import java.util.Set;
import javax.annotation.Nonnull;

public interface RemoteApplications
extends LinkedApplicationCapabilities {
    @Nonnull
    public Set<RemoteApplicationWithCapabilities> capableOf(@Nonnull CapabilityKey var1);

    @Nonnull
    public Set<RemoteApplicationWithCapabilities> capableOf(@Nonnull String var1);
}

