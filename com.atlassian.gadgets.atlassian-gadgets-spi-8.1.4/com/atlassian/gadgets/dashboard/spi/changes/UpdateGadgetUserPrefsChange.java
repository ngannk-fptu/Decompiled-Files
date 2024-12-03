/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetId
 */
package com.atlassian.gadgets.dashboard.spi.changes;

import com.atlassian.gadgets.GadgetId;
import com.atlassian.gadgets.dashboard.spi.changes.DashboardChange;
import java.util.Map;

public final class UpdateGadgetUserPrefsChange
implements DashboardChange {
    private final GadgetId gadgetId;
    private final Map<String, String> prefValues;

    public UpdateGadgetUserPrefsChange(GadgetId gadgetId, Map<String, String> prefValues) {
        this.gadgetId = gadgetId;
        this.prefValues = prefValues;
    }

    @Override
    public void accept(DashboardChange.Visitor visitor) {
        visitor.visit(this);
    }

    public GadgetId getGadgetId() {
        return this.gadgetId;
    }

    public Map<String, String> getPrefValues() {
        return this.prefValues;
    }
}

