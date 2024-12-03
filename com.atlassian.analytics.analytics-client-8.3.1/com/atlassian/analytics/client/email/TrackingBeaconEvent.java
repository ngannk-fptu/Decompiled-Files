/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  javax.annotation.Nonnull
 */
package com.atlassian.analytics.client.email;

import com.atlassian.analytics.api.annotations.EventName;
import javax.annotation.Nonnull;

public class TrackingBeaconEvent {
    private final String name;
    private final String product;

    public TrackingBeaconEvent(@Nonnull String name, @Nonnull String product) {
        this.name = name;
        this.product = product;
    }

    @EventName
    public String getEventName() {
        return this.product + ".email." + this.name + ".open";
    }
}

