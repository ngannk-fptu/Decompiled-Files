/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.gadgets.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="gadget.rest.gadgetInfo")
public class GadgetInfoRestFetchEvent {
    private final int gadgetCount;

    public GadgetInfoRestFetchEvent(int gadgetCount) {
        this.gadgetCount = gadgetCount;
    }

    public int getGadgetCount() {
        return this.gadgetCount;
    }
}

