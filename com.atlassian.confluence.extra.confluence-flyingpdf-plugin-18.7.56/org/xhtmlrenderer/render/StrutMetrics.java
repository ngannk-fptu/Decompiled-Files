/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

public class StrutMetrics {
    private int _baseline;
    private float _ascent;
    private float _descent;

    public StrutMetrics(float ascent, int baseline, float descent) {
        this._ascent = ascent;
        this._baseline = baseline;
        this._descent = descent;
    }

    public StrutMetrics() {
    }

    public float getAscent() {
        return this._ascent;
    }

    public void setAscent(float ascent) {
        this._ascent = ascent;
    }

    public int getBaseline() {
        return this._baseline;
    }

    public void setBaseline(int baseline) {
        this._baseline = baseline;
    }

    public float getDescent() {
        return this._descent;
    }

    public void setDescent(float descent) {
        this._descent = descent;
    }
}

