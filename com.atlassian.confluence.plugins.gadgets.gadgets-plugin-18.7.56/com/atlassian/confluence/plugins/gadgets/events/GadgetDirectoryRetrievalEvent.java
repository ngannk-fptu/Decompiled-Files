/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.gadgets.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="gadget.directory.retrieved")
public class GadgetDirectoryRetrievalEvent {
    private final int gadgetCount;

    public GadgetDirectoryRetrievalEvent(int gadgetCount) {
        this.gadgetCount = gadgetCount;
    }

    public int getGadgetCount() {
        return this.gadgetCount;
    }
}

