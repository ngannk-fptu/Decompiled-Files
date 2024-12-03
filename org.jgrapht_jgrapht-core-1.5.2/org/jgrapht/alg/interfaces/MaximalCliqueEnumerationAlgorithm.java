/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.interfaces;

import java.util.Iterator;
import java.util.Set;

public interface MaximalCliqueEnumerationAlgorithm<V, E>
extends Iterable<Set<V>> {
    @Override
    public Iterator<Set<V>> iterator();
}

