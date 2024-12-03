/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.ProfileRed;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;

public class ProfileRable
extends AbstractRable {
    private ICCColorSpaceWithIntent colorSpace;

    public ProfileRable(Filter src, ICCColorSpaceWithIntent colorSpace) {
        super(src);
        this.colorSpace = colorSpace;
    }

    public void setSource(Filter src) {
        this.init(src, null);
    }

    public Filter getSource() {
        return (Filter)this.getSources().get(0);
    }

    public void setColorSpace(ICCColorSpaceWithIntent colorSpace) {
        this.touch();
        this.colorSpace = colorSpace;
    }

    public ICCColorSpaceWithIntent getColorSpace() {
        return this.colorSpace;
    }

    @Override
    public RenderedImage createRendering(RenderContext rc) {
        RenderedImage srcRI = this.getSource().createRendering(rc);
        if (srcRI == null) {
            return null;
        }
        CachableRed srcCR = GraphicsUtil.wrap(srcRI);
        return new ProfileRed(srcCR, this.colorSpace);
    }
}

