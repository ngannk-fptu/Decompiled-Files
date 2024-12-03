/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonVersionBase;

public interface AvailableAddonWithVersionBase {
    public AddonBase getAddonBase();

    public AddonVersionBase getVersionBase();
}

