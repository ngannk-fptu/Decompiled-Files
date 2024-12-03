/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.Filter
 */
package org.apache.batik.gvt;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.event.GraphicsNodeChangeAdapter;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;

public class UpdateTracker
extends GraphicsNodeChangeAdapter {
    Map dirtyNodes = null;
    Map fromBounds = new HashMap();
    protected static Rectangle2D NULL_RECT = new Rectangle();

    public boolean hasChanged() {
        return this.dirtyNodes != null;
    }

    public List getDirtyAreas() {
        if (this.dirtyNodes == null) {
            return null;
        }
        LinkedList<Rectangle2D> ret = new LinkedList<Rectangle2D>();
        Set keys = this.dirtyNodes.keySet();
        for (Object key : keys) {
            WeakReference gnWRef = (WeakReference)key;
            GraphicsNode gn = (GraphicsNode)gnWRef.get();
            if (gn == null) continue;
            AffineTransform oat = (AffineTransform)this.dirtyNodes.get(gnWRef);
            if (oat != null) {
                oat = new AffineTransform(oat);
            }
            Rectangle2D srcORgn = (Rectangle2D)this.fromBounds.remove(gnWRef);
            Rectangle2D srcNRgn = null;
            AffineTransform nat = null;
            if (!(srcORgn instanceof ChngSrcRect)) {
                srcNRgn = gn.getBounds();
                nat = gn.getTransform();
                if (nat != null) {
                    nat = new AffineTransform(nat);
                }
            }
            while ((gn = gn.getParent()) != null) {
                Filter f = gn.getFilter();
                if (f != null) {
                    srcNRgn = f.getBounds2D();
                    nat = null;
                }
                AffineTransform at = gn.getTransform();
                gnWRef = gn.getWeakReference();
                AffineTransform poat = (AffineTransform)this.dirtyNodes.get(gnWRef);
                if (poat == null) {
                    poat = at;
                }
                if (poat != null) {
                    if (oat != null) {
                        oat.preConcatenate(poat);
                    } else {
                        oat = new AffineTransform(poat);
                    }
                }
                if (at == null) continue;
                if (nat != null) {
                    nat.preConcatenate(at);
                    continue;
                }
                nat = new AffineTransform(at);
            }
            if (gn != null) continue;
            Shape oRgn = srcORgn;
            if (oRgn != null && oRgn != NULL_RECT) {
                if (oat != null) {
                    oRgn = oat.createTransformedShape(srcORgn);
                }
                ret.add((Rectangle2D)oRgn);
            }
            if (srcNRgn == null) continue;
            Shape nRgn = srcNRgn;
            if (nat != null) {
                nRgn = nat.createTransformedShape(srcNRgn);
            }
            if (nRgn == null) continue;
            ret.add((Rectangle2D)nRgn);
        }
        this.fromBounds.clear();
        this.dirtyNodes.clear();
        return ret;
    }

    public Rectangle2D getNodeDirtyRegion(GraphicsNode gn, AffineTransform at) {
        WeakReference gnWRef = gn.getWeakReference();
        AffineTransform nat = (AffineTransform)this.dirtyNodes.get(gnWRef);
        if (nat == null) {
            nat = gn.getTransform();
        }
        if (nat != null) {
            at = new AffineTransform(at);
            at.concatenate(nat);
        }
        Filter f = gn.getFilter();
        Rectangle2D ret = null;
        if (gn instanceof CompositeGraphicsNode) {
            CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            for (Object aCgn : cgn) {
                GraphicsNode childGN = (GraphicsNode)aCgn;
                Rectangle2D r2d = this.getNodeDirtyRegion(childGN, at);
                if (r2d == null) continue;
                if (f != null) {
                    Shape s = at.createTransformedShape(f.getBounds2D());
                    ret = s.getBounds2D();
                    break;
                }
                if (ret == null || ret == NULL_RECT) {
                    ret = r2d;
                    continue;
                }
                ret.add(r2d);
            }
        } else {
            ret = (Rectangle2D)this.fromBounds.remove(gnWRef);
            if (ret == null) {
                ret = f != null ? f.getBounds2D() : gn.getBounds();
            } else if (ret == NULL_RECT) {
                ret = null;
            }
            if (ret != null) {
                ret = at.createTransformedShape(ret).getBounds2D();
            }
        }
        return ret;
    }

    public Rectangle2D getNodeDirtyRegion(GraphicsNode gn) {
        return this.getNodeDirtyRegion(gn, new AffineTransform());
    }

    @Override
    public void changeStarted(GraphicsNodeChangeEvent gnce) {
        GraphicsNode gn = gnce.getGraphicsNode();
        WeakReference gnWRef = gn.getWeakReference();
        boolean doPut = false;
        if (this.dirtyNodes == null) {
            this.dirtyNodes = new HashMap();
            doPut = true;
        } else if (!this.dirtyNodes.containsKey(gnWRef)) {
            doPut = true;
        }
        if (doPut) {
            AffineTransform at = gn.getTransform();
            at = at != null ? (AffineTransform)at.clone() : new AffineTransform();
            this.dirtyNodes.put(gnWRef, at);
        }
        GraphicsNode chngSrc = gnce.getChangeSrc();
        Rectangle2D rgn = null;
        if (chngSrc != null) {
            Rectangle2D drgn = this.getNodeDirtyRegion(chngSrc);
            if (drgn != null) {
                rgn = new ChngSrcRect(drgn);
            }
        } else {
            rgn = gn.getBounds();
        }
        Rectangle2D r2d = (Rectangle2D)this.fromBounds.remove(gnWRef);
        if (rgn != null) {
            if (r2d != null && r2d != NULL_RECT) {
                r2d.add(rgn);
            } else {
                r2d = rgn;
            }
        }
        if (r2d == null) {
            r2d = NULL_RECT;
        }
        this.fromBounds.put(gnWRef, r2d);
    }

    public void clear() {
        this.dirtyNodes = null;
    }

    static class ChngSrcRect
    extends Rectangle2D.Float {
        ChngSrcRect(Rectangle2D r2d) {
            super((float)r2d.getX(), (float)r2d.getY(), (float)r2d.getWidth(), (float)r2d.getHeight());
        }
    }
}

