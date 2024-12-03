/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetId
 */
package com.atlassian.gadgets.dashboard.spi.changes;

import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.dashboard.spi.changes.DashboardChange;

public final class RemoveGadgetChange
implements DashboardChange {
    private final GadgetId gadgetId;

    public RemoveGadgetChange(GadgetId gadgetId) {
        this.gadgetId = gadgetId;
    }

    @Override
    public void accept(DashboardChange.Visitor visitor) {
        visitor.visit(this);
    }

    public GadgetId getGadgetId() {
        return this.gadgetId;
    }
}

