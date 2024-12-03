/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class PlotOrientation
implements Serializable {
    private static final long serialVersionUID = -2508771828190337782L;
    public static final PlotOrientation HORIZONTAL = new PlotOrientation("PlotOrientation.HORIZONTAL");
    public static final PlotOrientation VERTICAL = new PlotOrientation("PlotOrientation.VERTICAL");
    private String name;

    private PlotOrientation(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PlotOrientation)) {
            return false;
        }
        PlotOrientation orientation = (PlotOrientation)obj;
        return this.name.equals(orientation.toString());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        PlotOrientation result = null;
        if (this.equals(HORIZONTAL)) {
            result = HORIZONTAL;
        } else if (this.equals(VERTICAL)) {
            result = VERTICAL;
        }
        return result;
    }
}

