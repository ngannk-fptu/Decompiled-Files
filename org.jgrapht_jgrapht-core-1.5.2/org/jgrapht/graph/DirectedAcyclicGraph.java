/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.graph.FastLookupGraphSpecificsStrategy;
import org.jgrapht.graph.GraphCycleProhibitedException;
import org.jgrapht.graph.GraphSpecificsStrategy;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.util.SupplierUtil;

public class DirectedAcyclicGraph<V, E>
extends AbstractBaseGraph<V, E>
implements Iterable<V> {
    private static final long serialVersionUID = 4522128427004938150L;
    private final Comparator<V> topoComparator;
    private final TopoOrderMap<V> topoOrderMap;
    private int maxTopoIndex = 0;
    private int minTopoIndex = 0;
    private transient long topoModCount = 0L;
    private final VisitedStrategyFactory visitedStrategyFactory;

    public DirectedAcyclicGraph(Class<? extends E> edgeClass) {
        this(null, SupplierUtil.createSupplier(edgeClass), false, false);
    }

    public DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted) {
        this(vertexSupplier, edgeSupplier, new VisitedBitSetImpl(), new TopoVertexBiMap(), weighted, false);
    }

    public DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted, boolean allowMultipleEdges) {
        this(vertexSupplier, edgeSupplier, new VisitedBitSetImpl(), new TopoVertexBiMap(), weighted, allowMultipleEdges);
    }

    public DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, boolean weighted, boolean allowMultipleEdges, GraphSpecificsStrategy<V, E> graphSpecificsStrategy) {
        this(vertexSupplier, edgeSupplier, new VisitedBitSetImpl(), new TopoVertexBiMap(), weighted, allowMultipleEdges, graphSpecificsStrategy);
    }

    protected DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, VisitedStrategyFactory visitedStrategyFactory, TopoOrderMap<V> topoOrderMap, boolean weighted) {
        this(vertexSupplier, edgeSupplier, visitedStrategyFactory, topoOrderMap, weighted, false);
    }

    protected DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, VisitedStrategyFactory visitedStrategyFactory, TopoOrderMap<V> topoOrderMap, boolean weighted, boolean allowMultipleEdges) {
        this(vertexSupplier, edgeSupplier, visitedStrategyFactory, topoOrderMap, weighted, allowMultipleEdges, new FastLookupGraphSpecificsStrategy());
    }

    protected DirectedAcyclicGraph(Supplier<V> vertexSupplier, Supplier<E> edgeSupplier, VisitedStrategyFactory visitedStrategyFactory, TopoOrderMap<V> topoOrderMap, boolean weighted, boolean allowMultipleEdges, GraphSpecificsStrategy<V, E> graphSpecificsStrategy) {
        super(vertexSupplier, edgeSupplier, new DefaultGraphType.Builder().directed().allowMultipleEdges(allowMultipleEdges).allowSelfLoops(false).weighted(weighted).allowCycles(false).build(), graphSpecificsStrategy);
        this.visitedStrategyFactory = Objects.requireNonNull(visitedStrategyFactory, "Visited factory cannot be null");
        this.topoOrderMap = Objects.requireNonNull(topoOrderMap, "Topological order map cannot be null");
        this.topoComparator = new TopoComparator();
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedAcyclicGraph<V, E>> createBuilder(Class<? extends E> edgeClass) {
        return new GraphBuilder(new DirectedAcyclicGraph<V, E>(edgeClass));
    }

    public static <V, E> GraphBuilder<V, E, ? extends DirectedAcyclicGraph<V, E>> createBuilder(Supplier<E> edgeSupplier) {
        return new GraphBuilder(new DirectedAcyclicGraph<V, E>(null, edgeSupplier, false));
    }

    @Override
    public V addVertex() {
        Object v = super.addVertex();
        if (v != null) {
            ++this.maxTopoIndex;
            this.topoOrderMap.putVertex(this.maxTopoIndex, v);
            ++this.topoModCount;
        }
        return v;
    }

    @Override
    public boolean addVertex(V v) {
        boolean added = super.addVertex(v);
        if (added) {
            ++this.maxTopoIndex;
            this.topoOrderMap.putVertex(this.maxTopoIndex, v);
            ++this.topoModCount;
        }
        return added;
    }

    @Override
    public boolean removeVertex(V v) {
        boolean removed = super.removeVertex(v);
        if (removed) {
            Integer topoIndex = this.topoOrderMap.removeVertex(v);
            if (topoIndex == this.minTopoIndex) {
                while (this.minTopoIndex < 0 && this.topoOrderMap.getVertex(this.minTopoIndex) == null) {
                    ++this.minTopoIndex;
                }
            }
            if (topoIndex == this.maxTopoIndex) {
                while (this.maxTopoIndex > 0 && this.topoOrderMap.getVertex(this.maxTopoIndex) == null) {
                    --this.maxTopoIndex;
                }
            }
            ++this.topoModCount;
        }
        return removed;
    }

    @Override
    public E addEdge(V sourceVertex, V targetVertex) {
        this.assertVertexExist(sourceVertex);
        this.assertVertexExist(targetVertex);
        try {
            this.updateDag(sourceVertex, targetVertex);
            return super.addEdge(sourceVertex, targetVertex);
        }
        catch (CycleFoundException e) {
            throw new GraphCycleProhibitedException();
        }
    }

    @Override
    public boolean addEdge(V sourceVertex, V targetVertex, E e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (this.containsEdge(e)) {
            return false;
        }
        this.assertVertexExist(sourceVertex);
        this.assertVertexExist(targetVertex);
        try {
            this.updateDag(sourceVertex, targetVertex);
            return super.addEdge(sourceVertex, targetVertex, e);
        }
        catch (CycleFoundException ex) {
            throw new GraphCycleProhibitedException();
        }
    }

    public Set<V> getAncestors(V vertex) {
        EdgeReversedGraph reversedGraph = new EdgeReversedGraph(this);
        DepthFirstIterator iterator = new DepthFirstIterator(reversedGraph, vertex);
        HashSet ancestors = new HashSet();
        if (iterator.hasNext()) {
            iterator.next();
        }
        iterator.forEachRemaining(ancestors::add);
        return ancestors;
    }

    public Set<V> getDescendants(V vertex) {
        DepthFirstIterator<V, Object> iterator = new DepthFirstIterator<V, Object>(this, vertex);
        HashSet descendants = new HashSet();
        if (iterator.hasNext()) {
            iterator.next();
        }
        iterator.forEachRemaining(descendants::add);
        return descendants;
    }

    @Override
    public Iterator<V> iterator() {
        return new TopoIterator();
    }

    private void updateDag(V sourceVertex, V targetVertex) throws CycleFoundException {
        Integer lb = this.topoOrderMap.getTopologicalIndex(targetVertex);
        Integer ub = this.topoOrderMap.getTopologicalIndex(sourceVertex);
        if (lb < ub) {
            HashSet df = new HashSet();
            HashSet db = new HashSet();
            Region affectedRegion = new Region(lb, ub);
            VisitedStrategy visited = this.visitedStrategyFactory.getVisitedStrategy(affectedRegion);
            this.dfsF(targetVertex, df, visited, affectedRegion);
            this.dfsB(sourceVertex, db, visited, affectedRegion);
            this.reorder(df, db, visited);
            ++this.topoModCount;
        }
    }

    private void dfsF(V initialVertex, Set<V> df, VisitedStrategy visited, Region affectedRegion) throws CycleFoundException {
        ArrayDeque vertices = new ArrayDeque();
        vertices.push(initialVertex);
        while (!vertices.isEmpty()) {
            Object vertex = vertices.pop();
            int topoIndex = this.topoOrderMap.getTopologicalIndex(vertex);
            if (visited.getVisited(topoIndex)) continue;
            visited.setVisited(topoIndex);
            df.add(vertex);
            for (Object outEdge : this.outgoingEdgesOf(vertex)) {
                Object nextVertex = this.getEdgeTarget(outEdge);
                Integer nextVertexTopoIndex = this.topoOrderMap.getTopologicalIndex(nextVertex);
                if (nextVertexTopoIndex == affectedRegion.finish) {
                    try {
                        for (V visitedVertex : df) {
                            visited.clearVisited(this.topoOrderMap.getTopologicalIndex(visitedVertex));
                        }
                    }
                    catch (UnsupportedOperationException unsupportedOperationException) {
                        // empty catch block
                    }
                    throw new CycleFoundException();
                }
                if (!affectedRegion.isIn(nextVertexTopoIndex) || visited.getVisited(nextVertexTopoIndex)) continue;
                vertices.push(nextVertex);
            }
        }
    }

    private void dfsB(V initialVertex, Set<V> db, VisitedStrategy visited, Region affectedRegion) {
        ArrayDeque vertices = new ArrayDeque();
        vertices.push(initialVertex);
        while (!vertices.isEmpty()) {
            Object vertex = vertices.pop();
            int topoIndex = this.topoOrderMap.getTopologicalIndex(vertex);
            if (visited.getVisited(topoIndex)) continue;
            visited.setVisited(topoIndex);
            db.add(vertex);
            for (Object inEdge : this.incomingEdgesOf(vertex)) {
                Object previousVertex = this.getEdgeSource(inEdge);
                Integer previousVertexTopoIndex = this.topoOrderMap.getTopologicalIndex(previousVertex);
                if (!affectedRegion.isIn(previousVertexTopoIndex) || visited.getVisited(previousVertexTopoIndex)) continue;
                vertices.push(previousVertex);
            }
        }
    }

    private void reorder(Set<V> df, Set<V> db, VisitedStrategy visited) {
        Integer topoIndex;
        ArrayList<V> topoDf = new ArrayList<V>(df);
        ArrayList<V> topoDb = new ArrayList<V>(db);
        topoDf.sort(this.topoComparator);
        topoDb.sort(this.topoComparator);
        TreeSet<Integer> availableTopoIndices = new TreeSet<Integer>();
        Object[] bigL = new Object[df.size() + db.size()];
        int lIndex = 0;
        boolean clearVisited = true;
        for (Object vertex : topoDb) {
            topoIndex = this.topoOrderMap.getTopologicalIndex(vertex);
            availableTopoIndices.add(topoIndex);
            bigL[lIndex++] = vertex;
            if (!clearVisited) continue;
            try {
                visited.clearVisited(topoIndex);
            }
            catch (UnsupportedOperationException e) {
                clearVisited = false;
            }
        }
        for (Object vertex : topoDf) {
            topoIndex = this.topoOrderMap.getTopologicalIndex(vertex);
            availableTopoIndices.add(topoIndex);
            bigL[lIndex++] = vertex;
            if (!clearVisited) continue;
            try {
                visited.clearVisited(topoIndex);
            }
            catch (UnsupportedOperationException e) {
                clearVisited = false;
            }
        }
        lIndex = 0;
        for (Integer topoIndex2 : availableTopoIndices) {
            Object vertex = bigL[lIndex++];
            this.topoOrderMap.putVertex(topoIndex2, vertex);
        }
    }

    protected static class VisitedBitSetImpl
    implements VisitedStrategy,
    VisitedStrategyFactory {
        private static final long serialVersionUID = 1L;
        private final BitSet visited = new BitSet();
        private Region affectedRegion;

        @Override
        public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
            this.affectedRegion = affectedRegion;
            return this;
        }

        @Override
        public void setVisited(int index) {
            this.visited.set(this.translateIndex(index), true);
        }

        @Override
        public boolean getVisited(int index) {
            return this.visited.get(this.translateIndex(index));
        }

        @Override
        public void clearVisited(int index) throws UnsupportedOperationException {
            this.visited.clear(this.translateIndex(index));
        }

        private int translateIndex(int index) {
            return index - this.affectedRegion.start;
        }
    }

    protected static class TopoVertexBiMap<V>
    implements TopoOrderMap<V> {
        private static final long serialVersionUID = 1L;
        private final Map<Integer, V> topoToVertex = new HashMap<Integer, V>();
        private final Map<V, Integer> vertexToTopo = new HashMap<V, Integer>();

        @Override
        public void putVertex(Integer index, V vertex) {
            this.topoToVertex.put(index, vertex);
            this.vertexToTopo.put((Integer)vertex, index);
        }

        @Override
        public V getVertex(Integer index) {
            return this.topoToVertex.get(index);
        }

        @Override
        public Integer getTopologicalIndex(V vertex) {
            return this.vertexToTopo.get(vertex);
        }

        @Override
        public Integer removeVertex(V vertex) {
            Integer topoIndex = this.vertexToTopo.remove(vertex);
            if (topoIndex != null) {
                this.topoToVertex.remove(topoIndex);
            }
            return topoIndex;
        }

        @Override
        public void removeAllVertices() {
            this.vertexToTopo.clear();
            this.topoToVertex.clear();
        }
    }

    protected static interface VisitedStrategyFactory
    extends Serializable {
        public VisitedStrategy getVisitedStrategy(Region var1);
    }

    protected static interface TopoOrderMap<V>
    extends Serializable {
        public void putVertex(Integer var1, V var2);

        public V getVertex(Integer var1);

        public Integer getTopologicalIndex(V var1);

        public Integer removeVertex(V var1);

        public void removeAllVertices();
    }

    private class TopoComparator
    implements Comparator<V>,
    Serializable {
        private static final long serialVersionUID = 8144905376266340066L;

        private TopoComparator() {
        }

        @Override
        public int compare(V o1, V o2) {
            return DirectedAcyclicGraph.this.topoOrderMap.getTopologicalIndex(o1).compareTo(DirectedAcyclicGraph.this.topoOrderMap.getTopologicalIndex(o2));
        }
    }

    private static class CycleFoundException
    extends Exception {
        private static final long serialVersionUID = 5583471522212552754L;

        private CycleFoundException() {
        }
    }

    private class TopoIterator
    implements Iterator<V> {
        private int currentTopoIndex;
        private final long expectedTopoModCount;
        private Integer nextIndex;

        public TopoIterator() {
            this.expectedTopoModCount = DirectedAcyclicGraph.this.topoModCount;
            this.nextIndex = null;
            this.currentTopoIndex = DirectedAcyclicGraph.this.minTopoIndex - 1;
        }

        @Override
        public boolean hasNext() {
            if (this.expectedTopoModCount != DirectedAcyclicGraph.this.topoModCount) {
                throw new ConcurrentModificationException();
            }
            this.nextIndex = this.getNextIndex();
            return this.nextIndex != null;
        }

        @Override
        public V next() {
            if (this.expectedTopoModCount != DirectedAcyclicGraph.this.topoModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.nextIndex == null) {
                this.nextIndex = this.getNextIndex();
            }
            if (this.nextIndex == null) {
                throw new NoSuchElementException();
            }
            this.currentTopoIndex = this.nextIndex;
            this.nextIndex = null;
            return DirectedAcyclicGraph.this.topoOrderMap.getVertex(this.currentTopoIndex);
        }

        @Override
        public void remove() {
            if (this.expectedTopoModCount != DirectedAcyclicGraph.this.topoModCount) {
                throw new ConcurrentModificationException();
            }
            Object vertexToRemove = DirectedAcyclicGraph.this.topoOrderMap.getVertex(this.currentTopoIndex);
            if (vertexToRemove == null) {
                throw new IllegalStateException();
            }
            DirectedAcyclicGraph.this.topoOrderMap.removeVertex(vertexToRemove);
        }

        private Integer getNextIndex() {
            for (int i = this.currentTopoIndex + 1; i <= DirectedAcyclicGraph.this.maxTopoIndex; ++i) {
                if (DirectedAcyclicGraph.this.topoOrderMap.getVertex(i) == null) continue;
                return i;
            }
            return null;
        }
    }

    protected static class Region
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final int start;
        private final int finish;

        public Region(int start, int finish) {
            if (start > finish) {
                throw new IllegalArgumentException("(start > finish): invariant broken");
            }
            this.start = start;
            this.finish = finish;
        }

        public int getSize() {
            return this.finish - this.start + 1;
        }

        public boolean isIn(int index) {
            return index >= this.start && index <= this.finish;
        }

        public int getStart() {
            return this.start;
        }

        public int getFinish() {
            return this.finish;
        }
    }

    protected static interface VisitedStrategy {
        public void setVisited(int var1);

        public boolean getVisited(int var1);

        public void clearVisited(int var1) throws UnsupportedOperationException;
    }

    protected static class VisitedArrayImpl
    implements VisitedStrategy,
    VisitedStrategyFactory {
        private static final long serialVersionUID = 1L;
        private final boolean[] visited;
        private final Region region;

        public VisitedArrayImpl() {
            this(null);
        }

        public VisitedArrayImpl(Region region) {
            if (region == null) {
                this.visited = null;
                this.region = null;
            } else {
                this.region = region;
                this.visited = new boolean[region.getSize()];
            }
        }

        @Override
        public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
            return new VisitedArrayImpl(affectedRegion);
        }

        @Override
        public void setVisited(int index) {
            this.visited[index - this.region.start] = true;
        }

        @Override
        public boolean getVisited(int index) {
            return this.visited[index - this.region.start];
        }

        @Override
        public void clearVisited(int index) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    protected static class VisitedHashSetImpl
    implements VisitedStrategy,
    VisitedStrategyFactory {
        private static final long serialVersionUID = 1L;
        private final Set<Integer> visited = new HashSet<Integer>();

        @Override
        public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
            this.visited.clear();
            return this;
        }

        @Override
        public void setVisited(int index) {
            this.visited.add(index);
        }

        @Override
        public boolean getVisited(int index) {
            return this.visited.contains(index);
        }

        @Override
        public void clearVisited(int index) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    protected static class VisitedArrayListImpl
    implements VisitedStrategy,
    VisitedStrategyFactory {
        private static final long serialVersionUID = 1L;
        private final List<Boolean> visited = new ArrayList<Boolean>();
        private Region affectedRegion;

        @Override
        public VisitedStrategy getVisitedStrategy(Region affectedRegion) {
            int minSize = affectedRegion.finish - affectedRegion.start + 1;
            while (this.visited.size() < minSize) {
                this.visited.add(Boolean.FALSE);
            }
            this.affectedRegion = affectedRegion;
            return this;
        }

        @Override
        public void setVisited(int index) {
            this.visited.set(this.translateIndex(index), Boolean.TRUE);
        }

        @Override
        public boolean getVisited(int index) {
            return this.visited.get(this.translateIndex(index));
        }

        @Override
        public void clearVisited(int index) throws UnsupportedOperationException {
            this.visited.set(this.translateIndex(index), Boolean.FALSE);
        }

        private int translateIndex(int index) {
            return index - this.affectedRegion.start;
        }
    }

    protected class TopoVertexMap
    implements TopoOrderMap<V> {
        private static final long serialVersionUID = 1L;
        private final List<V> topoToVertex = new ArrayList();
        private final Map<V, Integer> vertexToTopo = new HashMap();

        @Override
        public void putVertex(Integer index, V vertex) {
            int translatedIndex = this.translateIndex(index);
            while (translatedIndex + 1 > this.topoToVertex.size()) {
                this.topoToVertex.add(null);
            }
            this.topoToVertex.set(translatedIndex, vertex);
            this.vertexToTopo.put((Integer)vertex, index);
        }

        @Override
        public V getVertex(Integer index) {
            return this.topoToVertex.get(this.translateIndex(index));
        }

        @Override
        public Integer getTopologicalIndex(V vertex) {
            return this.vertexToTopo.get(vertex);
        }

        @Override
        public Integer removeVertex(V vertex) {
            Integer topoIndex = this.vertexToTopo.remove(vertex);
            if (topoIndex != null) {
                this.topoToVertex.set(this.translateIndex(topoIndex), null);
            }
            return topoIndex;
        }

        @Override
        public void removeAllVertices() {
            this.vertexToTopo.clear();
            this.topoToVertex.clear();
        }

        private int translateIndex(int index) {
            if (index >= 0) {
                return 2 * index;
            }
            return -1 * (index * 2 - 1);
        }
    }
}

