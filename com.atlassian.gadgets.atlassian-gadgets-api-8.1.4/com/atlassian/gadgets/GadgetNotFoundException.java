/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets;

import com.atlassian.gadgets.GadgetId;

public class GadgetNotFoundException
extends RuntimeException {
    private final GadgetId gadgetId;

    public GadgetNotFoundException(GadgetId gadgetId) {
        super("No such gadget with id " + gadgetId + " exists on this dashboard");
        this.gadgetId = gadgetId;
    }

    public GadgetId getGadgetId() {
        return this.gadgetId;
    }
}

