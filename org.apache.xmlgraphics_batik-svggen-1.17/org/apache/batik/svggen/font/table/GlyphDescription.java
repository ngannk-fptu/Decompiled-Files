/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

public interface GlyphDescription {
    public int getEndPtOfContours(int var1);

    public byte getFlags(int var1);

    public short getXCoordinate(int var1);

    public short getYCoordinate(int var1);

    public short getXMaximum();

    public short getXMinimum();

    public short getYMaximum();

    public short getYMinimum();

    public boolean isComposite();

    public int getPointCount();

    public int getContourCount();
}

