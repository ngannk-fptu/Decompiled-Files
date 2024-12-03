/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.probe.provider;

import org.glassfish.external.probe.provider.StatsProviderInfo;

public interface StatsProviderManagerDelegate {
    public void register(StatsProviderInfo var1);

    public void unregister(Object var1);

    public boolean hasListeners(String var1);
}

