/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.tuple;

import java.io.Serializable;

public class Pair<A, B>
implements Serializable,
Cloneable {
    private static final long serialVersionUID = 1L;
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        assert (first != null && second != null) : "both parameters must be non-null";
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> newInstance(A first, B second) {
        return new Pair<A, B>(first, second);
    }

    public A getFirst() {
        return this.first;
    }

    public B getSecond() {
        return this.second;
    }

    public String toString() {
        return "Pair [" + this.first + ", " + this.second + "]";
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.first == null ? 0 : this.first.hashCode());
        result = 31 * result + (this.second == null ? 0 : this.second.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Pair other = (Pair)obj;
        if (this.first == null ? other.first != null : !this.first.equals(other.first)) {
            return false;
        }
        return !(this.second == null ? other.second != null : !this.second.equals(other.second));
    }

    public Pair<A, B> clone() {
        return new Pair<A, B>(this.first, this.second);
    }
}

