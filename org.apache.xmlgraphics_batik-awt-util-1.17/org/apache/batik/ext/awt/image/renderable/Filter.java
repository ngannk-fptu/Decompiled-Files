/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.RenderableImage;

public interface Filter
extends RenderableImage {
    public Rectangle2D getBounds2D();

    public long getTimeStamp();

    public Shape getDependencyRegion(int var1, Rectangle2D var2);

    public Shape getDirtyRegion(int var1, Rectangle2D var2);
}

