/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface DirectedSimpleCycles<V, E> {
    default public List<List<V>> findSimpleCycles() {
        ArrayList<List<V>> result = new ArrayList<List<V>>();
        this.findSimpleCycles(result::add);
        return result;
    }

    public void findSimpleCycles(Consumer<List<V>> var1);
}

