/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.ttf;

public interface GlyphDescription {
    public int getEndPtOfContours(int var1);

    public byte getFlags(int var1);

    public short getXCoordinate(int var1);

    public short getYCoordinate(int var1);

    public boolean isComposite();

    public int getPointCount();

    public int getContourCount();

    public void resolve();
}

