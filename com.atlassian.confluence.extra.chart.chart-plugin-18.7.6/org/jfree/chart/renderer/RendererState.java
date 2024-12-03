/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.PlotRenderingInfo;

public class RendererState {
    private PlotRenderingInfo info;

    public RendererState(PlotRenderingInfo info) {
        this.info = info;
    }

    public PlotRenderingInfo getInfo() {
        return this.info;
    }

    public EntityCollection getEntityCollection() {
        ChartRenderingInfo owner;
        EntityCollection result = null;
        if (this.info != null && (owner = this.info.getOwner()) != null) {
            result = owner.getEntityCollection();
        }
        return result;
    }
}

