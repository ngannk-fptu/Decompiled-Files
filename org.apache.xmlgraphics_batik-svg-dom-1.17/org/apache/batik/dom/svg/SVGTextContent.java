/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public interface SVGTextContent {
    public int getNumberOfChars();

    public Rectangle2D getExtentOfChar(int var1);

    public Point2D getStartPositionOfChar(int var1);

    public Point2D getEndPositionOfChar(int var1);

    public float getRotationOfChar(int var1);

    public void selectSubString(int var1, int var2);

    public float getComputedTextLength();

    public float getSubStringLength(int var1, int var2);

    public int getCharNumAtPosition(float var1, float var2);
}

