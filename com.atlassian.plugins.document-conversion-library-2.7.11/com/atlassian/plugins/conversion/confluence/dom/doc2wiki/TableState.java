/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki;

import java.util.ArrayList;

class TableState {
    private ArrayList<Double> _currentColumns = new ArrayList();
    private double _rowLeft;

    public TableState() {
        this._currentColumns.add(0.0);
    }

    public double getRowLeft() {
        return this._rowLeft;
    }

    public void setRowLeft(double rowLeft) {
        this._rowLeft = rowLeft;
    }

    public ArrayList<Double> getCurrentColumns() {
        return this._currentColumns;
    }

    public void nextRow() {
        this._rowLeft = 0.0;
    }
}

