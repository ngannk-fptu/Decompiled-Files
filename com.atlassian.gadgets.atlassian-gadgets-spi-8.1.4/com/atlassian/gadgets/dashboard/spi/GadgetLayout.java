/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetId
 */
package com.atlassian.gadgets.dashboard.spi;

import com.atlassian.gadgets.GadgetId;
import java.util.Collections;
import java.util.List;

public final class GadgetLayout {
    private final List<? extends Iterable<GadgetId>> columnLayout;

    public GadgetLayout(List<? extends Iterable<GadgetId>> columnLayout) {
        this.columnLayout = Collections.unmodifiableList(columnLayout);
    }

    public int getNumberOfColumns() {
        return this.columnLayout.size();
    }

    public Iterable<GadgetId> getGadgetsInColumn(int column) {
        return this.columnLayout.get(column);
    }
}

