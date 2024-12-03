/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class DialShape
implements Serializable {
    private static final long serialVersionUID = -3471933055190251131L;
    public static final DialShape CIRCLE = new DialShape("DialShape.CIRCLE");
    public static final DialShape CHORD = new DialShape("DialShape.CHORD");
    public static final DialShape PIE = new DialShape("DialShape.PIE");
    private String name;

    private DialShape(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DialShape)) {
            return false;
        }
        DialShape shape = (DialShape)obj;
        return this.name.equals(shape.toString());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(CIRCLE)) {
            return CIRCLE;
        }
        if (this.equals(CHORD)) {
            return CHORD;
        }
        if (this.equals(PIE)) {
            return PIE;
        }
        return null;
    }
}

