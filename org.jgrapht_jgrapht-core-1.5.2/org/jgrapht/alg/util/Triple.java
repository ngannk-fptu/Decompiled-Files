/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.io.Serializable;
import java.util.Objects;

public class Triple<A, B, C>
implements Serializable {
    private static final long serialVersionUID = -7076291895521537427L;
    protected A first;
    protected B second;
    protected C third;

    public Triple(A a, B b, C c) {
        this.first = a;
        this.second = b;
        this.third = c;
    }

    public A getFirst() {
        return this.first;
    }

    public B getSecond() {
        return this.second;
    }

    public C getThird() {
        return this.third;
    }

    public void setFirst(A a) {
        this.first = a;
    }

    public void setSecond(B b) {
        this.second = b;
    }

    public void setThird(C c) {
        this.third = c;
    }

    public <E> boolean hasElement(E e) {
        if (e == null) {
            return this.first == null || this.second == null || this.third == null;
        }
        return e.equals(this.first) || e.equals(this.second) || e.equals(this.third);
    }

    public String toString() {
        return "(" + this.first + "," + this.second + "," + this.third + ")";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Triple)) {
            return false;
        }
        Triple other = (Triple)o;
        return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second) && Objects.equals(this.third, other.third);
    }

    public int hashCode() {
        return Objects.hash(this.first, this.second, this.third);
    }

    public static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
        return new Triple<A, B, C>(a, b, c);
    }
}

