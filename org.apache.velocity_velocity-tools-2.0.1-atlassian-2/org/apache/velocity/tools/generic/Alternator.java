/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.Collection;

public class Alternator {
    private Object[] list;
    private int index = 0;
    private boolean auto = true;

    public Alternator(Object ... list) {
        this(true, list);
    }

    public Alternator(boolean auto, Object ... list) {
        this.auto = auto;
        this.list = list.length == 1 && list[0] instanceof Collection ? ((Collection)list[0]).toArray() : list;
    }

    public boolean isAuto() {
        return this.auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public void shift() {
        this.index = (this.index + 1) % this.list.length;
    }

    public Object getCurrent() {
        return this.list[this.index];
    }

    public Object getNext() {
        Object o = this.getCurrent();
        this.shift();
        return o;
    }

    public String toString() {
        Object o = this.list[this.index];
        if (this.auto) {
            this.shift();
        }
        if (o == null) {
            return null;
        }
        return o.toString();
    }
}

