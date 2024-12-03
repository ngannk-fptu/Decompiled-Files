/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics.event;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.analytics.event.PluginAnalyticsEvent;
import java.util.Arrays;
import java.util.Collections;

public class PluginFailedToEnableAnalyticsEvent
extends PluginAnalyticsEvent {
    private final boolean installation;
    private final boolean marketplace;

    public PluginFailedToEnableAnalyticsEvent(Plugin plugin, DefaultHostApplicationInformation hostApplicationInformation, boolean installation, boolean marketplace, Option<String> sen) {
        super(plugin, hostApplicationInformation, sen);
        this.installation = installation;
        this.marketplace = marketplace;
    }

    public boolean isInstallation() {
        return this.installation;
    }

    public boolean isMarketplace() {
        return this.marketplace;
    }

    @Override
    public String getEventType() {
        return "enablement-failure";
    }

    @Override
    public Iterable<Pair<String, String>> getMetadata() {
        return Collections.unmodifiableList(Arrays.asList(Pair.pair("installation", Boolean.toString(this.isInstallation())), Pair.pair("marketplace", Boolean.toString(this.isMarketplace()))));
    }
}

