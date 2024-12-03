/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.isomorphism;

import java.util.Iterator;
import org.jgrapht.GraphMapping;

public interface IsomorphismInspector<V, E> {
    public Iterator<GraphMapping<V, E>> getMappings();

    public boolean isomorphismExists();
}

