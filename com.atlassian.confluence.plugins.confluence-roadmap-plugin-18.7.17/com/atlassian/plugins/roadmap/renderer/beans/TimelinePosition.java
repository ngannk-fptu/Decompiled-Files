/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.renderer.beans;

public class TimelinePosition {
    private int column;
    private double offset;
    private int columnOffset;

    public TimelinePosition() {
    }

    public TimelinePosition(int column, double offset, int columnOffset) {
        this.column = column;
        this.columnOffset = columnOffset;
        this.offset = offset;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public double getOffset() {
        return this.offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public int getColumnOffset() {
        return this.columnOffset;
    }

    public void setColumnOffset(int columnOffset) {
        this.columnOffset = columnOffset;
    }

    public String toString() {
        return String.format("[Column=%d, Offset=%f, ColumnOffset=%d]", this.column, this.offset, this.columnOffset);
    }
}

