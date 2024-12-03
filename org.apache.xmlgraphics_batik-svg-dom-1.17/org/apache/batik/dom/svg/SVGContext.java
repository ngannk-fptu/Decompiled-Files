/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public interface SVGContext {
    public static final int PERCENTAGE_FONT_SIZE = 0;
    public static final int PERCENTAGE_VIEWPORT_WIDTH = 1;
    public static final int PERCENTAGE_VIEWPORT_HEIGHT = 2;
    public static final int PERCENTAGE_VIEWPORT_SIZE = 3;

    public float getPixelUnitToMillimeter();

    public float getPixelToMM();

    public Rectangle2D getBBox();

    public AffineTransform getScreenTransform();

    public void setScreenTransform(AffineTransform var1);

    public AffineTransform getCTM();

    public AffineTransform getGlobalTransform();

    public float getViewportWidth();

    public float getViewportHeight();

    public float getFontSize();
}

