/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.io.Serializable;
import java.util.Objects;

public class Pair<A, B>
implements Serializable {
    private static final long serialVersionUID = 8176288675989092842L;
    protected A first;
    protected B second;

    public Pair(A a, B b) {
        this.first = a;
        this.second = b;
    }

    public A getFirst() {
        return this.first;
    }

    public B getSecond() {
        return this.second;
    }

    public void setFirst(A f) {
        this.first = f;
    }

    public void setSecond(B s) {
        this.second = s;
    }

    public <E> boolean hasElement(E e) {
        if (e == null) {
            return this.first == null || this.second == null;
        }
        return e.equals(this.first) || e.equals(this.second);
    }

    public String toString() {
        return "(" + this.first + "," + this.second + ")";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair other = (Pair)o;
        return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<A, B>(a, b);
    }
}

