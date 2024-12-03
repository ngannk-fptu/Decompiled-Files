/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.axis;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class AxisLocation
implements Serializable {
    private static final long serialVersionUID = -3276922179323563410L;
    public static final AxisLocation TOP_OR_LEFT = new AxisLocation("AxisLocation.TOP_OR_LEFT");
    public static final AxisLocation TOP_OR_RIGHT = new AxisLocation("AxisLocation.TOP_OR_RIGHT");
    public static final AxisLocation BOTTOM_OR_LEFT = new AxisLocation("AxisLocation.BOTTOM_OR_LEFT");
    public static final AxisLocation BOTTOM_OR_RIGHT = new AxisLocation("AxisLocation.BOTTOM_OR_RIGHT");
    private String name;

    private AxisLocation(String name) {
        this.name = name;
    }

    public AxisLocation getOpposite() {
        return AxisLocation.getOpposite(this);
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AxisLocation)) {
            return false;
        }
        AxisLocation location = (AxisLocation)obj;
        return this.name.equals(location.toString());
    }

    public static AxisLocation getOpposite(AxisLocation location) {
        if (location == null) {
            throw new IllegalArgumentException("Null 'location' argument.");
        }
        AxisLocation result = null;
        if (location == TOP_OR_LEFT) {
            result = BOTTOM_OR_RIGHT;
        } else if (location == TOP_OR_RIGHT) {
            result = BOTTOM_OR_LEFT;
        } else if (location == BOTTOM_OR_LEFT) {
            result = TOP_OR_RIGHT;
        } else if (location == BOTTOM_OR_RIGHT) {
            result = TOP_OR_LEFT;
        } else {
            throw new IllegalStateException("AxisLocation not recognised.");
        }
        return result;
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(TOP_OR_RIGHT)) {
            return TOP_OR_RIGHT;
        }
        if (this.equals(BOTTOM_OR_RIGHT)) {
            return BOTTOM_OR_RIGHT;
        }
        if (this.equals(TOP_OR_LEFT)) {
            return TOP_OR_LEFT;
        }
        if (this.equals(BOTTOM_OR_LEFT)) {
            return BOTTOM_OR_LEFT;
        }
        return null;
    }
}

