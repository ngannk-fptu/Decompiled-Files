/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterChainRable;
import org.apache.batik.ext.awt.image.renderable.FilterResRable;
import org.apache.batik.ext.awt.image.renderable.FilterResRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PadRable;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.renderable.PaintRable;

public class FilterChainRable8Bit
extends AbstractRable
implements FilterChainRable,
PaintRable {
    private int filterResolutionX;
    private int filterResolutionY;
    private Filter chainSource;
    private FilterResRable filterRes;
    private PadRable crop;
    private Rectangle2D filterRegion;

    public FilterChainRable8Bit(Filter source, Rectangle2D filterRegion) {
        if (source == null) {
            throw new IllegalArgumentException();
        }
        if (filterRegion == null) {
            throw new IllegalArgumentException();
        }
        Rectangle2D padRect = (Rectangle2D)filterRegion.clone();
        this.crop = new PadRable8Bit(source, padRect, PadMode.ZERO_PAD);
        this.chainSource = source;
        this.filterRegion = filterRegion;
        this.init(this.crop);
    }

    @Override
    public int getFilterResolutionX() {
        return this.filterResolutionX;
    }

    @Override
    public void setFilterResolutionX(int filterResolutionX) {
        this.touch();
        this.filterResolutionX = filterResolutionX;
        this.setupFilterRes();
    }

    @Override
    public int getFilterResolutionY() {
        return this.filterResolutionY;
    }

    @Override
    public void setFilterResolutionY(int filterResolutionY) {
        this.touch();
        this.filterResolutionY = filterResolutionY;
        this.setupFilterRes();
    }

    private void setupFilterRes() {
        if (this.filterResolutionX >= 0) {
            if (this.filterRes == null) {
                this.filterRes = new FilterResRable8Bit();
                this.filterRes.setSource(this.chainSource);
            }
            this.filterRes.setFilterResolutionX(this.filterResolutionX);
            this.filterRes.setFilterResolutionY(this.filterResolutionY);
        } else {
            this.filterRes = null;
        }
        if (this.filterRes != null) {
            this.crop.setSource(this.filterRes);
        } else {
            this.crop.setSource(this.chainSource);
        }
    }

    @Override
    public void setFilterRegion(Rectangle2D filterRegion) {
        if (filterRegion == null) {
            throw new IllegalArgumentException();
        }
        this.touch();
        this.filterRegion = filterRegion;
    }

    @Override
    public Rectangle2D getFilterRegion() {
        return this.filterRegion;
    }

    @Override
    public Filter getSource() {
        return this.crop;
    }

    @Override
    public void setSource(Filter chainSource) {
        if (chainSource == null) {
            throw new IllegalArgumentException("Null Source for Filter Chain");
        }
        this.touch();
        this.chainSource = chainSource;
        if (this.filterRes == null) {
            this.crop.setSource(chainSource);
        } else {
            this.filterRes.setSource(chainSource);
        }
    }

    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.filterRegion.clone();
    }

    @Override
    public boolean paintRable(Graphics2D g2d) {
        Composite c = g2d.getComposite();
        if (!SVGComposite.OVER.equals(c)) {
            return false;
        }
        GraphicsUtil.drawImage(g2d, this.getSource());
        return true;
    }

    @Override
    public RenderedImage createRendering(RenderContext context) {
        return this.crop.createRendering(context);
    }
}

