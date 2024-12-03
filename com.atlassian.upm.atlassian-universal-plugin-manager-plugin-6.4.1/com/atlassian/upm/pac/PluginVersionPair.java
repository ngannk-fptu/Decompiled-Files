/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.pac;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.upm.api.util.Option;

public class PluginVersionPair {
    private final Addon addon;
    private final Option<AddonVersion> specific;
    private final Option<AddonVersion> latest;

    public PluginVersionPair(Addon addon, Option<AddonVersion> specific, Option<AddonVersion> latest) {
        this.addon = addon;
        this.specific = specific;
        this.latest = latest;
    }

    public Addon getAddon() {
        return this.addon;
    }

    public Option<AddonVersion> getSpecific() {
        return this.specific;
    }

    public Option<AddonVersion> getLatest() {
        return this.latest;
    }
}

