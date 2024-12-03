/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.gadgets.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="gadget.macro.metadata.build")
public class GadgetMacroMetadataBuildEvent {
    private final int gadgetCount;

    public GadgetMacroMetadataBuildEvent(int gadgetCount) {
        this.gadgetCount = gadgetCount;
    }

    public int getGadgetCount() {
        return this.gadgetCount;
    }
}

