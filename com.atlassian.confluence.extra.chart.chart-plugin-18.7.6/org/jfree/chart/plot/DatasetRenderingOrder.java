/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.plot;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class DatasetRenderingOrder
implements Serializable {
    private static final long serialVersionUID = -600593412366385072L;
    public static final DatasetRenderingOrder FORWARD = new DatasetRenderingOrder("DatasetRenderingOrder.FORWARD");
    public static final DatasetRenderingOrder REVERSE = new DatasetRenderingOrder("DatasetRenderingOrder.REVERSE");
    private String name;

    private DatasetRenderingOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof DatasetRenderingOrder)) {
            return false;
        }
        DatasetRenderingOrder order = (DatasetRenderingOrder)obj;
        return this.name.equals(order.toString());
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(FORWARD)) {
            return FORWARD;
        }
        if (this.equals(REVERSE)) {
            return REVERSE;
        }
        return null;
    }
}

