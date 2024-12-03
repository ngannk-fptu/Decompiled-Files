/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.ext.awt.image.renderable.Filter
 */
package org.apache.batik.gvt;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.Map;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.gvt.filter.Mask;

public interface GraphicsNode {
    public static final int VISIBLE_PAINTED = 0;
    public static final int VISIBLE_FILL = 1;
    public static final int VISIBLE_STROKE = 2;
    public static final int VISIBLE = 3;
    public static final int PAINTED = 4;
    public static final int FILL = 5;
    public static final int STROKE = 6;
    public static final int ALL = 7;
    public static final int NONE = 8;
    public static final AffineTransform IDENTITY = new AffineTransform();

    public WeakReference getWeakReference();

    public int getPointerEventType();

    public void setPointerEventType(int var1);

    public void setTransform(AffineTransform var1);

    public AffineTransform getTransform();

    public AffineTransform getInverseTransform();

    public AffineTransform getGlobalTransform();

    public void setComposite(Composite var1);

    public Composite getComposite();

    public void setVisible(boolean var1);

    public boolean isVisible();

    public void setClip(ClipRable var1);

    public ClipRable getClip();

    public void setRenderingHint(RenderingHints.Key var1, Object var2);

    public void setRenderingHints(Map var1);

    public void setRenderingHints(RenderingHints var1);

    public RenderingHints getRenderingHints();

    public void setMask(Mask var1);

    public Mask getMask();

    public void setFilter(Filter var1);

    public Filter getFilter();

    public Filter getGraphicsNodeRable(boolean var1);

    public Filter getEnableBackgroundGraphicsNodeRable(boolean var1);

    public void paint(Graphics2D var1);

    public void primitivePaint(Graphics2D var1);

    public CompositeGraphicsNode getParent();

    public RootGraphicsNode getRoot();

    public Rectangle2D getBounds();

    public Rectangle2D getTransformedBounds(AffineTransform var1);

    public Rectangle2D getPrimitiveBounds();

    public Rectangle2D getTransformedPrimitiveBounds(AffineTransform var1);

    public Rectangle2D getGeometryBounds();

    public Rectangle2D getTransformedGeometryBounds(AffineTransform var1);

    public Rectangle2D getSensitiveBounds();

    public Rectangle2D getTransformedSensitiveBounds(AffineTransform var1);

    public boolean contains(Point2D var1);

    public boolean intersects(Rectangle2D var1);

    public GraphicsNode nodeHitAt(Point2D var1);

    public Shape getOutline();
}

