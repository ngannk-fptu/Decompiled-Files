/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.DashboardItemState
 *  com.atlassian.gadgets.GadgetState
 *  com.atlassian.gadgets.dashboard.DashboardState$ColumnIndex
 *  com.atlassian.plugin.util.Assertions
 */
package com.atlassian.gadgets.dashboard.spi.changes;

import com.atlassian.gadgets.DashboardItemState;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.dashboard.DashboardState;
import com.atlassian.gadgets.dashboard.spi.changes.DashboardChange;
import com.atlassian.plugin.util.Assertions;

public final class AddGadgetChange
implements DashboardChange {
    private final DashboardItemState state;
    private final DashboardState.ColumnIndex columnIndex;
    private final int rowIndex;

    public AddGadgetChange(DashboardItemState state, DashboardState.ColumnIndex columnIndex, int rowIndex) {
        this.state = (DashboardItemState)Assertions.notNull((String)"state", (Object)state);
        this.columnIndex = (DashboardState.ColumnIndex)Assertions.notNull((String)"columnIndex", (Object)columnIndex);
        this.rowIndex = rowIndex;
    }

    @Override
    public void accept(DashboardChange.Visitor visitor) {
        visitor.visit(this);
    }

    @Deprecated
    public GadgetState getState() {
        if (this.state instanceof GadgetState) {
            return (GadgetState)this.state;
        }
        throw new IllegalStateException("Unsupported local dashboard item");
    }

    public DashboardItemState getDashboardItemState() {
        return this.state;
    }

    public DashboardState.ColumnIndex getColumnIndex() {
        return this.columnIndex;
    }

    public int getRowIndex() {
        return this.rowIndex;
    }
}

