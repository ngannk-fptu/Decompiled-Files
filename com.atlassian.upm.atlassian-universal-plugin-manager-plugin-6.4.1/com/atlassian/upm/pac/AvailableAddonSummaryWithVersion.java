/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonSummary;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.marketplace.client.model.AddonVersionSummary;
import com.atlassian.upm.pac.AvailableAddonWithVersionBase;
import java.util.Iterator;

public class AvailableAddonSummaryWithVersion
implements AvailableAddonWithVersionBase {
    private final AddonSummary addon;
    private final AddonVersionSummary version;

    public AvailableAddonSummaryWithVersion(AddonSummary addon, AddonVersionSummary version) {
        this.addon = addon;
        this.version = version;
    }

    public static AvailableAddonSummaryWithVersion fromAddonSummary(AddonSummary addon) {
        Iterator iterator = addon.getVersion().iterator();
        if (iterator.hasNext()) {
            AddonVersionSummary v = (AddonVersionSummary)iterator.next();
            return new AvailableAddonSummaryWithVersion(addon, v);
        }
        return null;
    }

    @Override
    public AddonBase getAddonBase() {
        return this.addon;
    }

    @Override
    public AddonVersionBase getVersionBase() {
        return this.version;
    }

    public boolean equals(Object other) {
        if (other instanceof AvailableAddonSummaryWithVersion) {
            AvailableAddonSummaryWithVersion o = (AvailableAddonSummaryWithVersion)other;
            return this.addon.getKey().equals(o.addon.getKey()) && this.version.getName().equals(o.version.getName());
        }
        return false;
    }

    public int hashCode() {
        return this.addon.getKey().hashCode() + this.version.getName().hashCode();
    }
}

