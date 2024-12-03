/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.font.LineMetrics;
import org.xhtmlrenderer.render.FSFontMetrics;

public class LineMetricsAdapter
implements FSFontMetrics {
    private LineMetrics _lineMetrics;

    public LineMetricsAdapter(LineMetrics lineMetrics) {
        this._lineMetrics = lineMetrics;
    }

    @Override
    public float getAscent() {
        return this._lineMetrics.getAscent();
    }

    @Override
    public float getDescent() {
        return this._lineMetrics.getDescent();
    }

    @Override
    public float getStrikethroughOffset() {
        return this._lineMetrics.getStrikethroughOffset();
    }

    @Override
    public float getStrikethroughThickness() {
        return this._lineMetrics.getStrikethroughThickness();
    }

    @Override
    public float getUnderlineOffset() {
        return this._lineMetrics.getUnderlineOffset();
    }

    @Override
    public float getUnderlineThickness() {
        return this._lineMetrics.getUnderlineThickness();
    }
}

