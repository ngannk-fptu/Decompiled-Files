/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.util.DownscaleQuality;

public class ScalingOptions {
    private DownscaleQuality downscalingHint;
    private Object renderingHint;
    private int targetWidth;
    private int targetHeight;

    public ScalingOptions(DownscaleQuality downscalingHint, Object interpolationHint) {
        this.downscalingHint = downscalingHint;
        this.renderingHint = interpolationHint;
    }

    public ScalingOptions() {
        this(DownscaleQuality.FAST, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    public ScalingOptions(int targetWidth, int targetHeight, int type, DownscaleQuality downscalingHint, Object hint) {
        this(downscalingHint, hint);
        this.setTargetHeight(Math.max(1, targetHeight));
        this.setTargetWidth(Math.max(1, targetWidth));
    }

    public DownscaleQuality getDownscalingHint() {
        return this.downscalingHint;
    }

    public Object getRenderingHint() {
        return this.renderingHint;
    }

    public void applyRenderingHints(Graphics2D g2) {
        g2.setRenderingHints(this.getRenderingHints());
    }

    protected Map getRenderingHints() {
        HashMap<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
        map.put(RenderingHints.KEY_INTERPOLATION, this.getRenderingHint());
        return map;
    }

    public boolean sizeMatches(int w, int h) {
        return w == this.getTargetWidth() && h == this.getTargetHeight();
    }

    public boolean sizeMatches(Image img) {
        return this.sizeMatches(img.getWidth(null), img.getHeight(null));
    }

    public int getTargetWidth() {
        return this.targetWidth;
    }

    public int getTargetHeight() {
        return this.targetHeight;
    }

    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
    }

    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    public void setTargetDimensions(Dimension dim) {
        this.setTargetWidth((int)dim.getWidth());
        this.setTargetHeight((int)dim.getHeight());
    }
}

