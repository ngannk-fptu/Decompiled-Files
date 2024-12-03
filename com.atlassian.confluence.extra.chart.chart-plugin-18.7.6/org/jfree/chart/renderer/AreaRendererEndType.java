/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.renderer;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class AreaRendererEndType
implements Serializable {
    private static final long serialVersionUID = -1774146392916359839L;
    public static final AreaRendererEndType TAPER = new AreaRendererEndType("AreaRendererEndType.TAPER");
    public static final AreaRendererEndType TRUNCATE = new AreaRendererEndType("AreaRendererEndType.TRUNCATE");
    public static final AreaRendererEndType LEVEL = new AreaRendererEndType("AreaRendererEndType.LEVEL");
    private String name;

    private AreaRendererEndType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AreaRendererEndType)) {
            return false;
        }
        AreaRendererEndType t = (AreaRendererEndType)obj;
        return this.name.equals(t.toString());
    }

    private Object readResolve() throws ObjectStreamException {
        AreaRendererEndType result = null;
        if (this.equals(LEVEL)) {
            result = LEVEL;
        } else if (this.equals(TAPER)) {
            result = TAPER;
        } else if (this.equals(TRUNCATE)) {
            result = TRUNCATE;
        }
        return result;
    }
}

