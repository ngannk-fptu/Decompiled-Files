/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.tuple;

import aQute.libg.tuple.Pair;

public class ComparablePair<A extends Comparable<A>, B>
extends Pair<A, B>
implements Comparable<Pair<A, ?>> {
    private static final long serialVersionUID = 1L;

    public ComparablePair(A first, B second) {
        super(first, second);
    }

    @Override
    public int compareTo(Pair<A, ?> o) {
        return ((Comparable)this.getFirst()).compareTo(o.getFirst());
    }
}

