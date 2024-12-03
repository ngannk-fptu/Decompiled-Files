/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.util.Map;
import java.util.Vector;
import org.apache.batik.ext.awt.image.renderable.Filter;

public class DeferRable
implements Filter {
    volatile Filter src;
    Rectangle2D bounds;
    Map props;

    public synchronized Filter getSource() {
        while (this.src == null) {
            try {
                this.wait();
            }
            catch (InterruptedException interruptedException) {}
        }
        return this.src;
    }

    public synchronized void setSource(Filter src) {
        if (this.src != null) {
            return;
        }
        this.src = src;
        this.bounds = src.getBounds2D();
        this.notifyAll();
    }

    public synchronized void setBounds(Rectangle2D bounds) {
        if (this.bounds != null) {
            return;
        }
        this.bounds = bounds;
        this.notifyAll();
    }

    public synchronized void setProperties(Map props) {
        this.props = props;
        this.notifyAll();
    }

    @Override
    public long getTimeStamp() {
        return this.getSource().getTimeStamp();
    }

    public Vector getSources() {
        return this.getSource().getSources();
    }

    @Override
    public boolean isDynamic() {
        return this.getSource().isDynamic();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Rectangle2D getBounds2D() {
        DeferRable deferRable = this;
        synchronized (deferRable) {
            while (this.src == null && this.bounds == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }
        if (this.src != null) {
            return this.src.getBounds2D();
        }
        return this.bounds;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object getProperty(String name) {
        DeferRable deferRable = this;
        synchronized (deferRable) {
            while (this.src == null && this.props == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }
        if (this.src != null) {
            return this.src.getProperty(name);
        }
        return this.props.get(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String[] getPropertyNames() {
        DeferRable deferRable = this;
        synchronized (deferRable) {
            while (this.src == null && this.props == null) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }
        if (this.src != null) {
            return this.src.getPropertyNames();
        }
        String[] ret = new String[this.props.size()];
        this.props.keySet().toArray(ret);
        return ret;
    }

    @Override
    public RenderedImage createDefaultRendering() {
        return this.getSource().createDefaultRendering();
    }

    @Override
    public RenderedImage createScaledRendering(int w, int h, RenderingHints hints) {
        return this.getSource().createScaledRendering(w, h, hints);
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        return this.getSource().createRendering(rc);
    }

    @Override
    public Shape getDependencyRegion(int srcIndex, Rectangle2D outputRgn) {
        return this.getSource().getDependencyRegion(srcIndex, outputRgn);
    }

    @Override
    public Shape getDirtyRegion(int srcIndex, Rectangle2D inputRgn) {
        return this.getSource().getDirtyRegion(srcIndex, inputRgn);
    }
}

