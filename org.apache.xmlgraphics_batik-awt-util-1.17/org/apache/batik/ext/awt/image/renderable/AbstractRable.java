/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;

public abstract class AbstractRable
implements Filter {
    protected Vector srcs;
    protected Map props = new HashMap();
    protected long stamp = 0L;

    protected AbstractRable() {
        this.srcs = new Vector();
    }

    protected AbstractRable(Filter src) {
        this.init(src, null);
    }

    protected AbstractRable(Filter src, Map props) {
        this.init(src, props);
    }

    protected AbstractRable(List srcs) {
        this(srcs, null);
    }

    protected AbstractRable(List srcs, Map props) {
        this.init(srcs, props);
    }

    public final void touch() {
        ++this.stamp;
    }

    @Override
    public long getTimeStamp() {
        return this.stamp;
    }

    protected void init(Filter src) {
        this.touch();
        this.srcs = new Vector(1);
        if (src != null) {
            this.srcs.add(src);
        }
    }

    protected void init(Filter src, Map props) {
        this.init(src);
        if (props != null) {
            this.props.putAll(props);
        }
    }

    protected void init(List srcs) {
        this.touch();
        this.srcs = new Vector(srcs);
    }

    protected void init(List srcs, Map props) {
        this.init(srcs);
        if (props != null) {
            this.props.putAll(props);
        }
    }

    @Override
    public Rectangle2D getBounds2D() {
        Rectangle2D bounds = null;
        if (this.srcs.size() != 0) {
            Iterator i = this.srcs.iterator();
            Filter src = (Filter)i.next();
            bounds = (Rectangle2D)src.getBounds2D().clone();
            while (i.hasNext()) {
                src = (Filter)i.next();
                Rectangle2D r = src.getBounds2D();
                Rectangle2D.union(bounds, r, bounds);
            }
        }
        return bounds;
    }

    public Vector getSources() {
        return this.srcs;
    }

    @Override
    public RenderedImage createDefaultRendering() {
        return this.createScaledRendering(100, 100, null);
    }

    @Override
    public RenderedImage createScaledRendering(int w, int h, RenderingHints hints) {
        float sX = (float)w / this.getWidth();
        float sY = (float)h / this.getHeight();
        float scale = Math.min(sX, sY);
        AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
        RenderContext rc = new RenderContext(at, hints);
        float dX = this.getWidth() * scale - (float)w;
        float dY = this.getHeight() * scale - (float)h;
        RenderedImage ri = this.createRendering(rc);
        CachableRed cr = RenderedImageCachableRed.wrap(ri);
        return new PadRed(cr, new Rectangle((int)(dX / 2.0f), (int)(dY / 2.0f), w, h), PadMode.ZERO_PAD, null);
    }

    @Override
    public float getMinX() {
        return (float)this.getBounds2D().getX();
    }

    @Override
    public float getMinY() {
        return (float)this.getBounds2D().getY();
    }

    @Override
    public float getWidth() {
        return (float)this.getBounds2D().getWidth();
    }

    @Override
    public float getHeight() {
        return (float)this.getBounds2D().getHeight();
    }

    @Override
    public Object getProperty(String name) {
        Object ret = this.props.get(name);
        if (ret != null) {
            return ret;
        }
        for (Object src : this.srcs) {
            RenderableImage ri = (RenderableImage)src;
            ret = ri.getProperty(name);
            if (ret == null) continue;
            return ret;
        }
        return null;
    }

    @Override
    public String[] getPropertyNames() {
        Set keys = this.props.keySet();
        Iterator<Object> iter = keys.iterator();
        String[] ret = new String[keys.size()];
        int i = 0;
        while (iter.hasNext()) {
            ret[i++] = (String)iter.next();
        }
        for (RenderableImage ri : this.srcs) {
            String[] srcProps = ri.getPropertyNames();
            if (srcProps.length == 0) continue;
            String[] tmp = new String[ret.length + srcProps.length];
            System.arraycopy(ret, 0, tmp, 0, ret.length);
            System.arraycopy(tmp, ret.length, srcProps, 0, srcProps.length);
            ret = tmp;
        }
        return ret;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        if (srcIndex < 0 || srcIndex > this.srcs.size()) {
            throw new IndexOutOfBoundsException("Nonexistant source requested.");
        }
        Rectangle2D srect = (Rectangle2D)outputRgn.clone();
        Rectangle2D bounds = this.getBounds2D();
        if (!bounds.intersects(srect)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(srect, bounds, srect);
        return srect;
    }

    @Override
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        if (srcIndex < 0 || srcIndex > this.srcs.size()) {
            throw new IndexOutOfBoundsException("Nonexistant source requested.");
        }
        Rectangle2D drect = (Rectangle2D)inputRgn.clone();
        Rectangle2D bounds = this.getBounds2D();
        if (!bounds.intersects(drect)) {
            return new Rectangle2D.Float();
        }
        Rectangle2D.intersect(drect, bounds, drect);
        return drect;
    }
}

