/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.AddressableHeap$Handle
 *  org.jheaps.tree.FibonacciHeap
 */
package org.jgrapht.alg.spanning;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.util.CollectionUtil;
import org.jgrapht.util.VertexToIntegerMapping;
import org.jheaps.AddressableHeap;
import org.jheaps.tree.FibonacciHeap;

public class PrimMinimumSpanningTree<V, E>
implements SpanningTreeAlgorithm<E> {
    private final Graph<V, E> g;

    public PrimMinimumSpanningTree(Graph<V, E> graph) {
        this.g = Objects.requireNonNull(graph, "Graph cannot be null");
    }

    @Override
    public SpanningTreeAlgorithm.SpanningTree<E> getSpanningTree() {
        HashSet minimumSpanningTreeEdgeSet = CollectionUtil.newHashSetWithExpectedSize(this.g.vertexSet().size());
        double spanningTreeWeight = 0.0;
        int n = this.g.vertexSet().size();
        VertexToIntegerMapping<V> vertexToIntegerMapping = Graphs.getVertexToIntegerMapping(this.g);
        Map<V, Integer> vertexMap = vertexToIntegerMapping.getVertexMap();
        List<V> indexList = vertexToIntegerMapping.getIndexList();
        VertexInfo[] vertices = (VertexInfo[])Array.newInstance(VertexInfo.class, n);
        AddressableHeap.Handle[] fibNodes = (AddressableHeap.Handle[])Array.newInstance(AddressableHeap.Handle.class, n);
        FibonacciHeap fibonacciHeap = new FibonacciHeap();
        for (int i = 0; i < n; ++i) {
            vertices[i] = new VertexInfo();
            vertices[i].id = i;
            vertices[i].distance = Double.MAX_VALUE;
            fibNodes[i] = fibonacciHeap.insert((Object)vertices[i].distance, (Object)vertices[i]);
        }
        while (!fibonacciHeap.isEmpty()) {
            AddressableHeap.Handle fibNode = fibonacciHeap.deleteMin();
            VertexInfo vertexInfo = (VertexInfo)fibNode.getValue();
            V p = indexList.get(vertexInfo.id);
            vertexInfo.spanned = true;
            if (vertexInfo.edgeFromParent != null) {
                minimumSpanningTreeEdgeSet.add(vertexInfo.edgeFromParent);
                spanningTreeWeight += this.g.getEdgeWeight(vertexInfo.edgeFromParent);
            }
            for (E e : this.g.edgesOf(p)) {
                double cost;
                V q = Graphs.getOppositeVertex(this.g, e, p);
                int id = vertexMap.get(q);
                if (vertices[id].spanned || !((cost = this.g.getEdgeWeight(e)) < vertices[id].distance)) continue;
                vertices[id].distance = cost;
                vertices[id].edgeFromParent = e;
                fibNodes[id].decreaseKey((Object)cost);
            }
        }
        return new SpanningTreeAlgorithm.SpanningTreeImpl(minimumSpanningTreeEdgeSet, spanningTreeWeight);
    }

    private class VertexInfo {
        public int id;
        public boolean spanned;
        public double distance;
        public E edgeFromParent;

        private VertexInfo() {
        }
    }
}

