/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ListIterator;
import java.util.Vector;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.CompositeRable;
import org.apache.batik.ext.awt.image.renderable.CompositeRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterResRable;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;

public class FilterResRable8Bit
extends AbstractRable
implements FilterResRable,
PaintRable {
    private int filterResolutionX = -1;
    private int filterResolutionY = -1;
    Reference resRed = null;
    float resScale = 0.0f;

    public FilterResRable8Bit() {
    }

    public FilterResRable8Bit(Filter src, int filterResX, int filterResY) {
        this.init(src, null);
        this.setFilterResolutionX(filterResX);
        this.setFilterResolutionY(filterResY);
    }

    @Override
    public Filter getSource() {
        return (Filter)this.srcs.get(0);
    }

    @Override
    public void setSource(Filter src) {
        this.init(src, null);
    }

    @Override
    public int getFilterResolutionX() {
        return this.filterResolutionX;
    }

    @Override
    public void setFilterResolutionX(int filterResolutionX) {
        if (filterResolutionX < 0) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.filterResolutionX = filterResolutionX;
    }

    @Override
    public int getFilterResolutionY() {
        return this.filterResolutionY;
    }

    @Override
    public void setFilterResolutionY(int filterResolutionY) {
        this.touch();
        this.filterResolutionY = filterResolutionY;
    }

    public boolean allPaintRable(RenderableImage ri) {
        if (!(ri instanceof PaintRable)) {
            return false;
        }
        Vector<RenderableImage> v = ri.getSources();
        if (v == null) {
            return true;
        }
        for (Object e : v) {
            RenderableImage nri = (RenderableImage)e;
            if (this.allPaintRable(nri)) continue;
            return false;
        }
        return true;
    }

    public boolean distributeAcross(RenderableImage src, Graphics2D g2d) {
        if (src instanceof PadRable) {
            PadRable pad = (PadRable)src;
            Shape clip = g2d.getClip();
            g2d.clip(pad.getPadRect());
            boolean ret = this.distributeAcross(pad.getSource(), g2d);
            g2d.setClip(clip);
            return ret;
        }
        if (src instanceof CompositeRable) {
            CompositeRable comp = (CompositeRable)src;
            if (comp.getCompositeRule() != CompositeRule.OVER) {
                return false;
            }
            Vector<RenderableImage> v = comp.getSources();
            if (v == null) {
                return true;
            }
            ListIterator li = v.listIterator(v.size());
            while (li.hasPrevious()) {
                RenderableImage csrc = (RenderableImage)li.previous();
                if (this.allPaintRable(csrc)) continue;
                li.next();
                break;
            }
            if (!li.hasPrevious()) {
                GraphicsUtil.drawImage(g2d, comp);
                return true;
            }
            if (!li.hasNext()) {
                return false;
            }
            int idx = li.nextIndex();
            AbstractRable f = new CompositeRable8Bit(v.subList(0, idx), comp.getCompositeRule(), comp.isColorSpaceLinear());
            f = new FilterResRable8Bit(f, this.getFilterResolutionX(), this.getFilterResolutionY());
            GraphicsUtil.drawImage(g2d, f);
            while (li.hasNext()) {
                PaintRable pr = (PaintRable)li.next();
                if (pr.paintRable(g2d)) continue;
                Filter prf = (Filter)((Object)pr);
                prf = new FilterResRable8Bit(prf, this.getFilterResolutionX(), this.getFilterResolutionY());
                GraphicsUtil.drawImage(g2d, prf);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean paintRable(Graphics2D g2d) {
        Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        Filter src = this.getSource();
        return this.distributeAcross(src, g2d);
    }

    private float getResScale() {
        return this.resScale;
    }

    private RenderedImage getResRed(RenderingHints hints) {
        RenderedImage ret;
        double resScaleY;
        Rectangle2D imageRect = this.getBounds2D();
        double resScaleX = (double)this.getFilterResolutionX() / imageRect.getWidth();
        float resScale = (float)Math.min(resScaleX, resScaleY = (double)this.getFilterResolutionY() / imageRect.getHeight());
        if (resScale == this.resScale && (ret = (RenderedImage)this.resRed.get()) != null) {
            return ret;
        }
        AffineTransform resUsr2Dev = AffineTransform.getScaleInstance(resScale, resScale);
        RenderContext newRC = new RenderContext(resUsr2Dev, null, hints);
        ret = this.getSource().createRendering(newRC);
        ret = new TileCacheRed(GraphicsUtil.wrap(ret));
        this.resScale = resScale;
        this.resRed = new SoftReference<RenderedImage>(ret);
        return ret;
    }

    @Override
    public RenderedImage createRendering(RenderContext renderContext) {
        AffineTransform usr2dev = renderContext.getTransform();
        if (usr2dev == null) {
            usr2dev = new AffineTransform();
        }
        RenderingHints hints = renderContext.getRenderingHints();
        int filterResolutionX = this.getFilterResolutionX();
        int filterResolutionY = this.getFilterResolutionY();
        if (filterResolutionX <= 0 || filterResolutionY == 0) {
            return null;
        }
        Rectangle2D imageRect = this.getBounds2D();
        Rectangle devRect = usr2dev.createTransformedShape(imageRect).getBounds();
        float scaleX = 1.0f;
        if (filterResolutionX < devRect.width) {
            scaleX = (float)filterResolutionX / (float)devRect.width;
        }
        float scaleY = 1.0f;
        if (filterResolutionY < 0) {
            scaleY = scaleX;
        } else if (filterResolutionY < devRect.height) {
            scaleY = (float)filterResolutionY / (float)devRect.height;
        }
        if (scaleX >= 1.0f && scaleY >= 1.0f) {
            return this.getSource().createRendering(renderContext);
        }
        RenderedImage resRed = this.getResRed(hints);
        float resScale = this.getResScale();
        AffineTransform residualAT = new AffineTransform(usr2dev.getScaleX() / (double)resScale, usr2dev.getShearY() / (double)resScale, usr2dev.getShearX() / (double)resScale, usr2dev.getScaleY() / (double)resScale, usr2dev.getTranslateX(), usr2dev.getTranslateY());
        return new AffineRed(GraphicsUtil.wrap(resRed), residualAT, hints);
    }
}

