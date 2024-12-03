/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc.spatialdatatypes;

public class Figure {
    private byte figuresAttribute;
    private int pointOffset;

    public Figure(byte figuresAttribute, int pointOffset) {
        this.figuresAttribute = figuresAttribute;
        this.pointOffset = pointOffset;
    }

    public byte getFiguresAttribute() {
        return this.figuresAttribute;
    }

    public int getPointOffset() {
        return this.pointOffset;
    }

    public void setFiguresAttribute(byte fa) {
        this.figuresAttribute = fa;
    }
}

