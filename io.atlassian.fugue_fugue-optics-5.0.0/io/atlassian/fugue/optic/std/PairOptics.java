/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Pair
 */
package io.atlassian.fugue.optic.std;

import io.atlassian.fugue.Pair;
import io.atlassian.fugue.optic.Iso;
import io.atlassian.fugue.optic.Lens;
import io.atlassian.fugue.optic.PLens;
import java.util.AbstractMap;
import java.util.Map;

public final class PairOptics {
    private PairOptics() {
    }

    public static <A, B, C> PLens<Pair<A, B>, Pair<C, B>, A, C> pLeft() {
        return PLens.pLens(Pair::left, c -> ab -> Pair.pair((Object)c, (Object)ab.right()));
    }

    public static <A, B> Lens<Pair<A, B>, A> left() {
        return new Lens(PairOptics.pLeft());
    }

    public static <A, B, C> PLens<Pair<A, B>, Pair<A, C>, B, C> pRight() {
        return PLens.pLens(Pair::right, c -> ab -> Pair.pair((Object)ab.left(), (Object)c));
    }

    public static <A, B> Lens<Pair<A, B>, B> _right() {
        return new Lens(PairOptics.pRight());
    }

    public static <A, B> Iso<Pair<A, B>, Map.Entry<A, B>> pairToEntry() {
        return Iso.iso(p -> new AbstractMap.SimpleImmutableEntry<Object, Object>(p.left(), p.right()), e -> Pair.pair(e.getKey(), e.getValue()));
    }
}

