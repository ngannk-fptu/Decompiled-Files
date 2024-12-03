/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.util;

public final class Tuple2<X, Y> {
    public final X element1;
    public final Y element2;

    private Tuple2(X element1, Y element2) {
        this.element1 = element1;
        this.element2 = element2;
    }

    public static <X, Y> Tuple2<X, Y> of(X element1, Y element2) {
        return new Tuple2<X, Y>(element1, element2);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Tuple2 tuple2 = (Tuple2)o;
        if (this.element1 != null ? !this.element1.equals(tuple2.element1) : tuple2.element1 != null) {
            return false;
        }
        return this.element2 != null ? this.element2.equals(tuple2.element2) : tuple2.element2 == null;
    }

    public int hashCode() {
        int result = this.element1 != null ? this.element1.hashCode() : 0;
        result = 31 * result + (this.element2 != null ? this.element2.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Tuple2{element1=" + this.element1 + ", element2=" + this.element2 + '}';
    }
}

