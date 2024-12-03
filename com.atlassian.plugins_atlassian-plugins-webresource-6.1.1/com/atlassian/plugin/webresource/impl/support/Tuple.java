/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.plugin.webresource.impl.support.Support;
import com.google.common.base.Objects;

public class Tuple<A, B> {
    private final A first;
    private final B last;

    public Tuple(A first, B last) {
        this.first = first;
        this.last = last;
    }

    public A getFirst() {
        return this.first;
    }

    public B getLast() {
        return this.last;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Tuple other = (Tuple)o;
        return Support.equals(this.first, other.first) && Support.equals(this.last, other.last);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.first, this.last});
    }

    public String toString() {
        return this.first + ", " + this.last;
    }
}

