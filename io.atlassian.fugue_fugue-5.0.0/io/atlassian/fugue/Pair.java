/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Iterables;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Pair<A, B>
implements Serializable {
    private static final long serialVersionUID = 3054071035067921893L;
    private static final int HALF_WORD = 16;
    private final A left;
    private final B right;

    public static <A, B> Pair<A, B> pair(A left, B right) {
        return new Pair<A, B>(left, right);
    }

    public static <A, B> BiFunction<A, B, Pair<A, B>> pairs() {
        return Pair::pair;
    }

    public static <A> Function<Pair<A, ?>, A> leftValue() {
        return Pair::left;
    }

    public static <B> Function<Pair<?, B>, B> rightValue() {
        return Pair::right;
    }

    public static <A, B> Pair<B, B> ap(Pair<A, A> aa, Pair<Function<A, B>, Function<A, B>> ff) {
        return Pair.pair(ff.left().apply(aa.left()), ff.right().apply(aa.right()));
    }

    public static <A, B> Pair<B, B> map(Pair<A, A> aa, Function<A, B> f) {
        return Pair.pair(f.apply(aa.left()), f.apply(aa.right()));
    }

    public static <A, B> Iterable<Pair<A, B>> zip(Iterable<A> as, Iterable<B> bs) {
        return Iterables.zip(as, bs);
    }

    public static <A, B> Optional<Pair<A, B>> zip(Optional<A> oA, Optional<B> oB) {
        if (oA.isPresent() && oB.isPresent()) {
            return Optional.of(Pair.pair(oA.get(), oB.get()));
        }
        return Optional.empty();
    }

    public Pair(A left, B right) {
        this.left = Objects.requireNonNull(left, "Left parameter must not be null.");
        this.right = Objects.requireNonNull(right, "Right parameter must not be null.");
    }

    public A left() {
        return this.left;
    }

    public B right() {
        return this.right;
    }

    public String toString() {
        return "Pair(" + this.left + ", " + this.right + ")";
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair that = (Pair)o;
        return this.left.equals(that.left) && this.right.equals(that.right);
    }

    public int hashCode() {
        int lh = this.left.hashCode();
        int rh = this.right.hashCode();
        return (lh >> 16 ^ lh) << 16 | (rh << 16 ^ rh) >> 16;
    }
}

