/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.awt.color.ColorSpace;
import java.awt.image.RenderedImage;
import java.util.List;
import java.util.Map;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.AbstractRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public abstract class AbstractColorInterpolationRable
extends AbstractRable {
    protected boolean csLinear = true;

    protected AbstractColorInterpolationRable() {
    }

    protected AbstractColorInterpolationRable(Filter src) {
        super(src);
    }

    protected AbstractColorInterpolationRable(Filter src, Map props) {
        super(src, props);
    }

    protected AbstractColorInterpolationRable(List srcs) {
        super(srcs);
    }

    protected AbstractColorInterpolationRable(List srcs, Map props) {
        super(srcs, props);
    }

    public boolean isColorSpaceLinear() {
        return this.csLinear;
    }

    public void setColorSpaceLinear(boolean csLinear) {
        this.touch();
        this.csLinear = csLinear;
    }

    public ColorSpace getOperationColorSpace() {
        if (this.csLinear) {
            return ColorSpace.getInstance(1004);
        }
        return ColorSpace.getInstance(1000);
    }

    protected CachableRed convertSourceCS(CachableRed cr) {
        if (this.csLinear) {
            return GraphicsUtil.convertToLsRGB(cr);
        }
        return GraphicsUtil.convertTosRGB(cr);
    }

    protected CachableRed convertSourceCS(RenderedImage ri) {
        return this.convertSourceCS(GraphicsUtil.wrap(ri));
    }
}

