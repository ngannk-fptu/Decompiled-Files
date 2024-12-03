/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.dashboard.Layout
 */
package com.atlassian.gadgets.dashboard.spi.changes;

import com.atlassian.gadgets.dashboard.Layout;
import com.atlassian.gadgets.dashboard.spi.GadgetLayout;
import com.atlassian.gadgets.dashboard.spi.changes.DashboardChange;

public final class UpdateLayoutChange
implements DashboardChange {
    private final Layout layout;
    private final GadgetLayout gadgetLayout;

    public UpdateLayoutChange(Layout layout, GadgetLayout gadgetLayout) {
        this.layout = layout;
        this.gadgetLayout = gadgetLayout;
    }

    @Override
    public void accept(DashboardChange.Visitor visitor) {
        visitor.visit(this);
    }

    public Layout getLayout() {
        return this.layout;
    }

    public GadgetLayout getGadgetLayout() {
        return this.gadgetLayout;
    }
}

