/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface Graphics2DImagePainter {
    public void paint(Graphics2D var1, Rectangle2D var2);

    public Dimension getImageSize();
}

