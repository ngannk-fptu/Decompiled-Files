/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import javax.swing.table.AbstractTableModel;

public abstract class SortableTableModel
extends AbstractTableModel {
    private int sortingColumn = -1;
    private boolean ascending = true;

    public int getSortingColumn() {
        return this.sortingColumn;
    }

    public boolean isAscending() {
        return this.ascending;
    }

    public void setAscending(boolean flag) {
        this.ascending = flag;
    }

    public void sortByColumn(int column, boolean ascending) {
        if (this.isSortable(column)) {
            this.sortingColumn = column;
        }
    }

    public boolean isSortable(int column) {
        return false;
    }
}

