/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.block;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class LengthConstraintType
implements Serializable {
    private static final long serialVersionUID = -1156658804028142978L;
    public static final LengthConstraintType NONE = new LengthConstraintType("LengthConstraintType.NONE");
    public static final LengthConstraintType RANGE = new LengthConstraintType("RectangleConstraintType.RANGE");
    public static final LengthConstraintType FIXED = new LengthConstraintType("LengthConstraintType.FIXED");
    private String name;

    private LengthConstraintType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LengthConstraintType)) {
            return false;
        }
        LengthConstraintType that = (LengthConstraintType)obj;
        return this.name.equals(that.toString());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(NONE)) {
            return NONE;
        }
        if (this.equals(RANGE)) {
            return RANGE;
        }
        if (this.equals(FIXED)) {
            return FIXED;
        }
        return null;
    }
}

