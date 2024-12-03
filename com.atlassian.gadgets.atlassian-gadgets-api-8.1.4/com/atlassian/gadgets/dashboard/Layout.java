/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.gadgets.dashboard.DashboardState;

public enum Layout {
    A(ColumnSpec.FAIR),
    AA(ColumnSpec.FAIR, ColumnSpec.FAIR),
    AB(ColumnSpec.FAIR, ColumnSpec.GREEDY),
    BA(ColumnSpec.GREEDY, ColumnSpec.FAIR),
    AAA(ColumnSpec.FAIR, ColumnSpec.FAIR, ColumnSpec.FAIR),
    ABA(ColumnSpec.FAIR, ColumnSpec.GREEDY, ColumnSpec.FAIR);

    private final ColumnSpec[] columnSpec;

    private Layout(ColumnSpec ... columnSpec) {
        this.columnSpec = columnSpec;
    }

    public int getNumberOfColumns() {
        return this.columnSpec.length;
    }

    public boolean contains(DashboardState.ColumnIndex column) {
        return column.index() < this.columnSpec.length;
    }

    public Iterable<DashboardState.ColumnIndex> getColumnRange() {
        return DashboardState.ColumnIndex.range(DashboardState.ColumnIndex.ZERO, DashboardState.ColumnIndex.from(this.columnSpec.length - 1));
    }

    public boolean isColumnSizingFair(DashboardState.ColumnIndex column) {
        if (!this.contains(column)) {
            throw new IllegalArgumentException("Column " + (Object)((Object)column) + " does not exist in this layout");
        }
        return this.columnSpec[column.index()] == ColumnSpec.FAIR;
    }

    static enum ColumnSpec {
        GREEDY,
        FAIR;

    }
}

