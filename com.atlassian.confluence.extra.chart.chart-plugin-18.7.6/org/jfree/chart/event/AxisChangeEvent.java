/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.event;

import org.jfree.chart.axis.Axis;
import org.jfree.chart.event.ChartChangeEvent;

public class AxisChangeEvent
extends ChartChangeEvent {
    private Axis axis;

    public AxisChangeEvent(Axis axis) {
        super(axis);
        this.axis = axis;
    }

    public Axis getAxis() {
        return this.axis;
    }
}

