/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.GraphicsUtil
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.AbstractRable
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.FilterAsAlphaRable
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.ext.awt.image.rendered.CachableRed
 *  org.apache.batik.ext.awt.image.rendered.MultiplyAlphaRed
 *  org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed
 */
package org.apache.batik.gvt.filter;

import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterAsAlphaRable;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.MultiplyAlphaRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.Mask;

public class MaskRable8Bit
extends AbstractRable
implements Mask {
    protected GraphicsNode mask;
    protected Rectangle2D filterRegion;

    public MaskRable8Bit(Filter src, GraphicsNode mask, Rectangle2D filterRegion) {
        super(src, null);
        this.setMaskNode(mask);
        this.setFilterRegion(filterRegion);
    }

    @Override
    public void setSource(Filter src) {
        this.init(src, null);
    }

    @Override
    public Filter getSource() {
        return (Filter)this.getSources().get(0);
    }

    @Override
    public Rectangle2D getFilterRegion() {
        return (Rectangle2D)this.filterRegion.clone();
    }

    @Override
    public void setFilterRegion(Rectangle2D filterRegion) {
        if (filterRegion == null) {
            throw new IllegalArgumentException();
        }
        this.filterRegion = filterRegion;
    }

    @Override
    public void setMaskNode(GraphicsNode mask) {
        this.touch();
        this.mask = mask;
    }

    @Override
    public GraphicsNode getMaskNode() {
        return this.mask;
    }

    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.filterRegion.clone();
    }

    public RenderedImage createRendering(RenderContext rc) {
        Filter maskSrc = this.getMaskNode().getGraphicsNodeRable(true);
        PadRable8Bit maskPad = new PadRable8Bit(maskSrc, this.getBounds2D(), PadMode.ZERO_PAD);
        RenderedImage ri = (maskSrc = new FilterAsAlphaRable((Filter)maskPad)).createRendering(rc);
        if (ri == null) {
            return null;
        }
        CachableRed maskCr = RenderedImageCachableRed.wrap((RenderedImage)ri);
        PadRable8Bit maskedPad = new PadRable8Bit(this.getSource(), this.getBounds2D(), PadMode.ZERO_PAD);
        ri = maskedPad.createRendering(rc);
        if (ri == null) {
            return null;
        }
        CachableRed cr = GraphicsUtil.wrap((RenderedImage)ri);
        cr = GraphicsUtil.convertToLsRGB((CachableRed)cr);
        MultiplyAlphaRed ret = new MultiplyAlphaRed(cr, maskCr);
        return ret;
    }
}

