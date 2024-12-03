/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Pair;

public interface Semigroup<A> {
    public A append(A var1, A var2);

    default public A sumNonEmpty(A head, Iterable<A> tail) {
        A currentValue = head;
        for (A a : tail) {
            currentValue = this.append(currentValue, a);
        }
        return currentValue;
    }

    default public A multiply1p(int n, A a) {
        if (n <= 0) {
            return a;
        }
        A xTmp = a;
        int yTmp = n;
        A zTmp = a;
        while (true) {
            if ((yTmp & 1) == 1) {
                zTmp = this.append(xTmp, zTmp);
                if (yTmp == 1) {
                    return zTmp;
                }
            }
            xTmp = this.append(xTmp, xTmp);
            yTmp >>>= 1;
        }
    }

    public static <A, B> Semigroup<Pair<A, B>> compose(final Semigroup<A> sa, final Semigroup<B> sb) {
        return new Semigroup<Pair<A, B>>(){

            @Override
            public Pair<A, B> append(Pair<A, B> ab1, Pair<A, B> ab2) {
                return Pair.pair(sa.append(ab1.left(), ab2.left()), sb.append(ab1.right(), ab2.right()));
            }

            @Override
            public Pair<A, B> multiply1p(int n, Pair<A, B> ab) {
                return Pair.pair(sa.multiply1p(n, ab.left()), sb.multiply1p(n, ab.right()));
            }
        };
    }

    public static <A> Semigroup<A> dual(final Semigroup<A> semigroup) {
        return new Semigroup<A>(){

            @Override
            public A append(A a1, A a2) {
                return semigroup.append(a2, a1);
            }

            @Override
            public A multiply1p(int n, A a) {
                return semigroup.multiply1p(n, a);
            }
        };
    }
}

