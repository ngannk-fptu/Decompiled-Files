/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import org.jgrapht.util.TypeUtil;

class MaskVertexSet<V>
extends AbstractSet<V>
implements Serializable {
    private static final long serialVersionUID = 3751931017141472763L;
    private final Set<V> vertexSet;
    private final Predicate<V> mask;

    public MaskVertexSet(Set<V> vertexSet, Predicate<V> mask) {
        this.vertexSet = vertexSet;
        this.mask = mask;
    }

    @Override
    public boolean contains(Object o) {
        if (!this.vertexSet.contains(o)) {
            return false;
        }
        Object v = TypeUtil.uncheckedCast(o);
        return !this.mask.test(v);
    }

    @Override
    public Iterator<V> iterator() {
        return this.vertexSet.stream().filter(this.mask.negate()).iterator();
    }

    @Override
    public int size() {
        return (int)this.vertexSet.stream().filter(this.mask.negate()).count();
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator().hasNext();
    }
}

