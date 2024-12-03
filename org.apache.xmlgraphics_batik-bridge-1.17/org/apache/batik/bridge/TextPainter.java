/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.TextNode;

public interface TextPainter {
    public void paint(TextNode var1, Graphics2D var2);

    public Mark selectAt(double var1, double var3, TextNode var5);

    public Mark selectTo(double var1, double var3, Mark var5);

    public Mark selectFirst(TextNode var1);

    public Mark selectLast(TextNode var1);

    public Mark getMark(TextNode var1, int var2, boolean var3);

    public int[] getSelected(Mark var1, Mark var2);

    public Shape getHighlightShape(Mark var1, Mark var2);

    public Shape getOutline(TextNode var1);

    public Rectangle2D getBounds2D(TextNode var1);

    public Rectangle2D getGeometryBounds(TextNode var1);
}

