/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetId
 *  com.atlassian.gadgets.dashboard.Color
 *  com.atlassian.plugin.util.Assertions
 */
package com.atlassian.gadgets.dashboard.spi.changes;

import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.dashboard.Color;
import com.atlassian.gadgets.dashboard.spi.changes.DashboardChange;
import com.atlassian.plugin.util.Assertions;

public final class GadgetColorChange
implements DashboardChange {
    private final GadgetId gadgetId;
    private final Color color;

    public GadgetColorChange(GadgetId gadgetId, Color color) {
        this.gadgetId = (GadgetId)Assertions.notNull((String)"gadgetId", (Object)gadgetId);
        this.color = (Color)Assertions.notNull((String)"color", (Object)color);
    }

    @Override
    public void accept(DashboardChange.Visitor visitor) {
        visitor.visit(this);
    }

    public Color getColor() {
        return this.color;
    }

    public GadgetId getGadgetId() {
        return this.gadgetId;
    }
}

