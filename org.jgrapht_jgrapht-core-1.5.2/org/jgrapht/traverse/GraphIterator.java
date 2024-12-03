/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.Iterator;
import org.jgrapht.event.TraversalListener;

public interface GraphIterator<V, E>
extends Iterator<V> {
    public boolean isCrossComponentTraversal();

    public boolean isReuseEvents();

    public void setReuseEvents(boolean var1);

    public void addTraversalListener(TraversalListener<V, E> var1);

    public void removeTraversalListener(TraversalListener<V, E> var1);

    @Override
    public void remove();
}

