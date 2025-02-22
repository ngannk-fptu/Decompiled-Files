/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.util;

public final class Tuple3<X, Y, Z> {
    public final X element1;
    public final Y element2;
    public final Z element3;

    private Tuple3(X element1, Y element2, Z element3) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
    }

    public static <X, Y, Z> Tuple3<X, Y, Z> of(X element1, Y element2, Z element3) {
        return new Tuple3<X, Y, Z>(element1, element2, element3);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Tuple3 tuple3 = (Tuple3)o;
        if (this.element1 != null ? !this.element1.equals(tuple3.element1) : tuple3.element1 != null) {
            return false;
        }
        if (this.element2 != null ? !this.element2.equals(tuple3.element2) : tuple3.element2 != null) {
            return false;
        }
        return this.element3 != null ? this.element3.equals(tuple3.element3) : tuple3.element3 == null;
    }

    public int hashCode() {
        int result = this.element1 != null ? this.element1.hashCode() : 0;
        result = 31 * result + (this.element2 != null ? this.element2.hashCode() : 0);
        result = 31 * result + (this.element3 != null ? this.element3.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Tuple3{element1=" + this.element1 + ", element2=" + this.element2 + ", element3=" + this.element3 + '}';
    }
}

