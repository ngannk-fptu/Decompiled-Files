/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot.dial;

import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.plot.dial.DialLayer;

public class DialLayerChangeEvent
extends ChartChangeEvent {
    private DialLayer layer;

    public DialLayerChangeEvent(DialLayer layer) {
        super(layer);
        this.layer = layer;
    }

    public DialLayer getDialLayer() {
        return this.layer;
    }
}

