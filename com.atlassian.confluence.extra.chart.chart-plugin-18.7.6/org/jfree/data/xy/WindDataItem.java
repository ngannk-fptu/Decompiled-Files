/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xy;

import java.io.Serializable;

class WindDataItem
implements Comparable,
Serializable {
    private Number x;
    private Number windDir;
    private Number windForce;

    public WindDataItem(Number x, Number windDir, Number windForce) {
        this.x = x;
        this.windDir = windDir;
        this.windForce = windForce;
    }

    public Number getX() {
        return this.x;
    }

    public Number getWindDirection() {
        return this.windDir;
    }

    public Number getWindForce() {
        return this.windForce;
    }

    public int compareTo(Object object) {
        if (object instanceof WindDataItem) {
            WindDataItem item = (WindDataItem)object;
            if (this.x.doubleValue() > item.x.doubleValue()) {
                return 1;
            }
            if (this.x.equals(item.x)) {
                return 0;
            }
            return -1;
        }
        throw new ClassCastException("WindDataItem.compareTo(error)");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return false;
        }
        if (!(obj instanceof WindDataItem)) {
            return false;
        }
        WindDataItem that = (WindDataItem)obj;
        if (!this.x.equals(that.x)) {
            return false;
        }
        if (!this.windDir.equals(that.windDir)) {
            return false;
        }
        return this.windForce.equals(that.windForce);
    }
}

