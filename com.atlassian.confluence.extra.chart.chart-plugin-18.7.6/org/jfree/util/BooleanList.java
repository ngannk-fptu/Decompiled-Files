/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import org.jfree.util.AbstractObjectList;

public class BooleanList
extends AbstractObjectList {
    private static final long serialVersionUID = -8543170333219422042L;

    public Boolean getBoolean(int index) {
        return (Boolean)this.get(index);
    }

    public void setBoolean(int index, Boolean b) {
        this.set(index, b);
    }

    public boolean equals(Object o) {
        if (o instanceof BooleanList) {
            return super.equals(o);
        }
        return false;
    }

    public int hashCode() {
        return super.hashCode();
    }
}

