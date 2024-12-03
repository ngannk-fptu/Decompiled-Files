/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Iterables;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.Serializable;

public final class Pair<A, B>
implements Serializable {
    private static final long serialVersionUID = 3054071035067921893L;
    private static final int HALF_WORD = 16;
    private final A left;
    private final B right;

    public static <A, B> Pair<A, B> pair(A left, B right) {
        return new Pair<A, B>(left, right);
    }

    public static <A, B> Function2<A, B, Pair<A, B>> pairs() {
        return new Function2<A, B, Pair<A, B>>(){

            @Override
            public Pair<A, B> apply(A a, B b) {
                return Pair.pair(a, b);
            }
        };
    }

    public static <A> Function<Pair<A, ?>, A> leftValue() {
        return new LeftAccessor();
    }

    public static <B> Function<Pair<?, B>, B> rightValue() {
        return new RightAccessor();
    }

    public static <A, B> Iterable<Pair<A, B>> zip(Iterable<A> as, Iterable<B> bs) {
        return Iterables.zip(as, bs);
    }

    public Pair(A left, B right) {
        this.left = Preconditions.checkNotNull(left, (Object)"Left parameter must not be null.");
        this.right = Preconditions.checkNotNull(right, (Object)"Right parameter must not be null.");
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

    static class RightAccessor<B>
    implements Function<Pair<?, B>, B> {
        RightAccessor() {
        }

        public B apply(Pair<?, B> from) {
            return from.right();
        }
    }

    static class LeftAccessor<A>
    implements Function<Pair<A, ?>, A> {
        LeftAccessor() {
        }

        public A apply(Pair<A, ?> from) {
            return from.left();
        }
    }
}

