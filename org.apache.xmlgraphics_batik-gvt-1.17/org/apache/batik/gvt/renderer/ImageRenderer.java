/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.renderer;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import org.apache.batik.gvt.renderer.Renderer;

public interface ImageRenderer
extends Renderer {
    @Override
    public void dispose();

    public void updateOffScreen(int var1, int var2);

    @Override
    public void setTransform(AffineTransform var1);

    @Override
    public AffineTransform getTransform();

    public void setRenderingHints(RenderingHints var1);

    public RenderingHints getRenderingHints();

    public BufferedImage getOffScreen();

    public void clearOffScreen();

    public void flush();

    public void flush(Rectangle var1);

    public void flush(Collection var1);
}

