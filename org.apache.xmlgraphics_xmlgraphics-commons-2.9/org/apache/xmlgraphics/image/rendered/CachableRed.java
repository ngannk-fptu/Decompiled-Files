/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.rendered;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.RenderedImage;

public interface CachableRed
extends RenderedImage {
    public Rectangle getBounds();

    public Shape getDependencyRegion(int var1, Rectangle var2);

    public Shape getDirtyRegion(int var1, Rectangle var2);
}

