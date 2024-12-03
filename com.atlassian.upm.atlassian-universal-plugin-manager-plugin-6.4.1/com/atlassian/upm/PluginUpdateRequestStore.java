/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.upm.core.Plugin;

public interface PluginUpdateRequestStore {
    public void requestPluginUpdate(Plugin var1);

    public void requestPluginUpdate(Addon var1);

    public void resetPluginUpdateRequest(Plugin var1);

    public boolean isPluginUpdateRequested(Plugin var1);
}

