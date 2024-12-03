/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.ColorSpaceHintKey;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.FilterAlphaRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;

public class FilterAlphaRable
extends AbstractRable {
    public FilterAlphaRable(Filter src) {
        super(src, null);
    }

    public Filter getSource() {
        return (Filter)this.getSources().get(0);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return this.getSource().getBounds2D();
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        Shape aoi;
        AffineTransform at = rc.getTransform();
        RenderingHints rh = rc.getRenderingHints();
        if (rh == null) {
            rh = new RenderingHints(null);
        }
        if ((aoi = rc.getAreaOfInterest()) == null) {
            aoi = this.getBounds2D();
        }
        rh.put(RenderingHintsKeyExt.KEY_COLORSPACE, ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA);
        RenderedImage ri = this.getSource().createRendering(new RenderContext(at, aoi, rh));
        if (ri == null) {
            return null;
        }
        CachableRed cr = RenderedImageCachableRed.wrap(ri);
        Object val = cr.getProperty("org.apache.batik.gvt.filter.Colorspace");
        if (val == ColorSpaceHintKey.VALUE_COLORSPACE_ALPHA) {
            return cr;
        }
        return new FilterAlphaRed(cr);
    }
}

