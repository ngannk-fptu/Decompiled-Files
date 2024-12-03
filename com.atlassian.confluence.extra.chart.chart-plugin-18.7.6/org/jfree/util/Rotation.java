/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class Rotation
implements Serializable {
    private static final long serialVersionUID = -4662815260201591676L;
    public static final Rotation CLOCKWISE = new Rotation("Rotation.CLOCKWISE", -1.0);
    public static final Rotation ANTICLOCKWISE = new Rotation("Rotation.ANTICLOCKWISE", 1.0);
    private String name;
    private double factor;

    private Rotation(String name, double factor) {
        this.name = name;
        this.factor = factor;
    }

    public String toString() {
        return this.name;
    }

    public double getFactor() {
        return this.factor;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rotation)) {
            return false;
        }
        Rotation rotation = (Rotation)o;
        return this.factor == rotation.factor;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.factor);
        return (int)(temp ^ temp >>> 32);
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(CLOCKWISE)) {
            return CLOCKWISE;
        }
        if (this.equals(ANTICLOCKWISE)) {
            return ANTICLOCKWISE;
        }
        return null;
    }
}

