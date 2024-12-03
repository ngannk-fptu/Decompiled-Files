/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.RectListManager
 */
package org.apache.batik.gvt.renderer;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.apache.batik.ext.awt.geom.RectListManager;
import org.apache.batik.gvt.GraphicsNode;

public interface Renderer {
    public void setTree(GraphicsNode var1);

    public GraphicsNode getTree();

    public void repaint(Shape var1);

    public void repaint(RectListManager var1);

    public void setTransform(AffineTransform var1);

    public AffineTransform getTransform();

    public boolean isDoubleBuffered();

    public void setDoubleBuffered(boolean var1);

    public void dispose();
}

