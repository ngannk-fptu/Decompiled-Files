/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.traverse;

import java.util.ArrayDeque;
import java.util.Deque;
import org.jgrapht.Graph;
import org.jgrapht.traverse.CrossComponentIterator;
import org.jgrapht.util.TypeUtil;

public class DepthFirstIterator<V, E>
extends CrossComponentIterator<V, E, VisitColor> {
    public static final Object SENTINEL = new Object();
    private Deque<Object> stack = new ArrayDeque<Object>();

    public DepthFirstIterator(Graph<V, E> g) {
        this((Graph<Object, E>)g, null);
    }

    public DepthFirstIterator(Graph<V, E> g, V startVertex) {
        super(g, startVertex);
    }

    public DepthFirstIterator(Graph<V, E> g, Iterable<V> startVertices) {
        super(g, startVertices);
    }

    @Override
    protected boolean isConnectedComponentExhausted() {
        while (!this.stack.isEmpty()) {
            if (this.stack.getLast() != SENTINEL) {
                return false;
            }
            this.stack.removeLast();
            this.recordFinish();
        }
        return true;
    }

    @Override
    protected void encounterVertex(V vertex, E edge) {
        this.putSeenData(vertex, VisitColor.WHITE);
        this.stack.addLast(vertex);
    }

    @Override
    protected void encounterVertexAgain(V vertex, E edge) {
        VisitColor color = (VisitColor)((Object)this.getSeenData(vertex));
        if (color != VisitColor.WHITE) {
            return;
        }
        boolean found = this.stack.removeLastOccurrence(vertex);
        assert (found);
        this.stack.addLast(vertex);
    }

    @Override
    protected V provideNextVertex() {
        Object o;
        while ((o = this.stack.removeLast()) == SENTINEL) {
            this.recordFinish();
        }
        Object v = TypeUtil.uncheckedCast(o);
        this.stack.addLast(v);
        this.stack.addLast(SENTINEL);
        this.putSeenData(v, VisitColor.GRAY);
        return (V)v;
    }

    private void recordFinish() {
        Object v = TypeUtil.uncheckedCast(this.stack.removeLast());
        this.putSeenData(v, VisitColor.BLACK);
        this.finishVertex(v);
    }

    public Deque<Object> getStack() {
        return this.stack;
    }

    protected static enum VisitColor {
        WHITE,
        GRAY,
        BLACK;

    }
}

