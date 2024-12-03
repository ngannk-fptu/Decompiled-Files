/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.statistics;

import java.io.ObjectStreamException;
import java.io.Serializable;

public class HistogramType
implements Serializable {
    private static final long serialVersionUID = 2618927186251997727L;
    public static final HistogramType FREQUENCY = new HistogramType("FREQUENCY");
    public static final HistogramType RELATIVE_FREQUENCY = new HistogramType("RELATIVE_FREQUENCY");
    public static final HistogramType SCALE_AREA_TO_1 = new HistogramType("SCALE_AREA_TO_1");
    private String name;

    private HistogramType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HistogramType)) {
            return false;
        }
        HistogramType t = (HistogramType)obj;
        return this.name.equals(t.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(FREQUENCY)) {
            return FREQUENCY;
        }
        if (this.equals(RELATIVE_FREQUENCY)) {
            return RELATIVE_FREQUENCY;
        }
        if (this.equals(SCALE_AREA_TO_1)) {
            return SCALE_AREA_TO_1;
        }
        return null;
    }
}

