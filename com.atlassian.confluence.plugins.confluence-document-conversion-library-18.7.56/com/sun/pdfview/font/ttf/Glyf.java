/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.font.ttf;

import com.sun.pdfview.font.ttf.GlyfCompound;
import com.sun.pdfview.font.ttf.GlyfSimple;
import java.nio.ByteBuffer;

public class Glyf {
    private boolean isCompound;
    private short numContours;
    private short minX;
    private short minY;
    private short maxX;
    private short maxY;

    protected Glyf() {
    }

    public static Glyf getGlyf(ByteBuffer data) {
        short numContours = data.getShort();
        Glyf g = null;
        if (numContours == 0) {
            g = new Glyf();
        } else if (numContours == -1) {
            g = new GlyfCompound();
        } else if (numContours > 0) {
            g = new GlyfSimple();
        } else {
            throw new IllegalArgumentException("Unknown glyf type: " + numContours);
        }
        g.setNumContours(numContours);
        g.setMinX(data.getShort());
        g.setMinY(data.getShort());
        g.setMaxX(data.getShort());
        g.setMaxY(data.getShort());
        g.setData(data);
        return g;
    }

    public void setData(ByteBuffer data) {
    }

    public ByteBuffer getData() {
        ByteBuffer buf = ByteBuffer.allocate(this.getLength());
        buf.putShort(this.getNumContours());
        buf.putShort(this.getMinX());
        buf.putShort(this.getMinY());
        buf.putShort(this.getMaxX());
        buf.putShort(this.getMaxY());
        return buf;
    }

    public short getLength() {
        return 10;
    }

    public boolean isCompound() {
        return this.isCompound;
    }

    protected void setCompound(boolean isCompound) {
        this.isCompound = isCompound;
    }

    public short getNumContours() {
        return this.numContours;
    }

    protected void setNumContours(short numContours) {
        this.numContours = numContours;
    }

    public short getMinX() {
        return this.minX;
    }

    protected void setMinX(short minX) {
        this.minX = minX;
    }

    public short getMinY() {
        return this.minY;
    }

    protected void setMinY(short minY) {
        this.minY = minY;
    }

    public short getMaxX() {
        return this.maxX;
    }

    protected void setMaxX(short maxX) {
        this.maxX = maxX;
    }

    public short getMaxY() {
        return this.maxY;
    }

    protected void setMaxY(short maxY) {
        this.maxY = maxY;
    }
}

