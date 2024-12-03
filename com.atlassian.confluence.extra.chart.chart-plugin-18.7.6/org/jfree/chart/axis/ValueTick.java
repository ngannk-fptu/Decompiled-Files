/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import org.jfree.chart.axis.Tick;
import org.jfree.chart.axis.TickType;
import org.jfree.ui.TextAnchor;

public abstract class ValueTick
extends Tick {
    private double value;
    private TickType tickType;

    public ValueTick(double value, String label, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
        this(TickType.MAJOR, value, label, textAnchor, rotationAnchor, angle);
        this.value = value;
    }

    public ValueTick(TickType tickType, double value, String label, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
        super(label, textAnchor, rotationAnchor, angle);
        this.value = value;
        this.tickType = tickType;
    }

    public double getValue() {
        return this.value;
    }

    public TickType getTickType() {
        return this.tickType;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ValueTick)) {
            return false;
        }
        ValueTick that = (ValueTick)obj;
        if (this.value != that.value) {
            return false;
        }
        if (!this.tickType.equals(that.tickType)) {
            return false;
        }
        return super.equals(obj);
    }
}

