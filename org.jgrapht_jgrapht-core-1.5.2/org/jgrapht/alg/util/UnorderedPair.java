/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.util;

import java.io.Serializable;
import java.util.Objects;
import org.jgrapht.alg.util.Pair;

public class UnorderedPair<A, B>
extends Pair<A, B>
implements Serializable {
    private static final long serialVersionUID = -3110454174542533876L;

    public UnorderedPair(A a, B b) {
        super(a, b);
    }

    @Override
    public String toString() {
        return "{" + this.first + "," + this.second + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnorderedPair)) {
            return false;
        }
        UnorderedPair other = (UnorderedPair)o;
        return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second) || Objects.equals(this.first, other.second) && Objects.equals(this.second, other.first);
    }

    @Override
    public int hashCode() {
        int hash1 = this.first == null ? 0 : this.first.hashCode();
        int hash2 = this.second == null ? 0 : this.second.hashCode();
        return hash1 > hash2 ? hash1 * 31 + hash2 : hash2 * 31 + hash1;
    }

    public static <A, B> UnorderedPair<A, B> of(A a, B b) {
        return new UnorderedPair<A, B>(a, b);
    }
}

