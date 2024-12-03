/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import org.apache.batik.ext.awt.image.renderable.Filter;

public interface FloodRable
extends Filter {
    public void setFloodPaint(Paint var1);

    public Paint getFloodPaint();

    public void setFloodRegion(Rectangle2D var1);

    public Rectangle2D getFloodRegion();
}

