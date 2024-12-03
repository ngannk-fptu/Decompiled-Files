/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonBase;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.marketplace.client.model.AddonVersionBase;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.pac.AvailableAddonWithVersionBase;
import java.util.Iterator;
import java.util.function.Function;

public class AvailableAddonWithVersion
implements AvailableAddonWithVersionBase {
    private final Addon addon;
    private final AddonVersion version;

    public AvailableAddonWithVersion(Addon addon, AddonVersion version) {
        this.addon = addon;
        this.version = version;
    }

    public static Option<AvailableAddonWithVersion> fromAddon(Addon addon) {
        Iterator iterator = addon.getVersion().iterator();
        if (iterator.hasNext()) {
            AddonVersion v = (AddonVersion)iterator.next();
            return Option.some(new AvailableAddonWithVersion(addon, v));
        }
        return Option.none();
    }

    public Addon getAddon() {
        return this.addon;
    }

    @Override
    public AddonBase getAddonBase() {
        return this.addon;
    }

    public AddonVersion getVersion() {
        return this.version;
    }

    @Override
    public AddonVersionBase getVersionBase() {
        return this.version;
    }

    public boolean equals(Object other) {
        if (other instanceof AvailableAddonWithVersion) {
            AvailableAddonWithVersion o = (AvailableAddonWithVersion)other;
            return this.addon.getKey().equals(o.addon.getKey()) && this.version.getName().equals(o.version.getName());
        }
        return false;
    }

    public int hashCode() {
        return this.addon.getKey().hashCode() + this.version.getName().hashCode();
    }

    public static Function<AvailableAddonWithVersion, Addon> toAddon() {
        return AvailableAddonWithVersion::getAddon;
    }

    public static Function<AvailableAddonWithVersion, String> toAddonKey() {
        return input -> input.getAddon().getKey();
    }

    public static Function<AvailableAddonWithVersion, AddonVersion> toVersion() {
        return AvailableAddonWithVersion::getVersion;
    }
}

