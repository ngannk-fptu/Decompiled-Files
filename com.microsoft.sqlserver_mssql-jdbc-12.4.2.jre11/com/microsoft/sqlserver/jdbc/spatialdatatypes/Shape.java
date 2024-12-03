/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.spatialdatatypes;

public class Shape {
    private int parentOffset;
    private int figureOffset;
    private byte openGISType;

    public Shape(int parentOffset, int figureOffset, byte openGISType) {
        this.parentOffset = parentOffset;
        this.figureOffset = figureOffset;
        this.openGISType = openGISType;
    }

    public int getParentOffset() {
        return this.parentOffset;
    }

    public int getFigureOffset() {
        return this.figureOffset;
    }

    public byte getOpenGISType() {
        return this.openGISType;
    }

    public void setFigureOffset(int fo) {
        this.figureOffset = fo;
    }
}

