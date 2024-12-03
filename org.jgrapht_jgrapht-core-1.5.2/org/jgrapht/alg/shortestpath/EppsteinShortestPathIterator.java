/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.shortestpath;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.GraphType;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

public class EppsteinShortestPathIterator<V, E>
implements Iterator<GraphPath<V, E>> {
    private final Graph<V, E> graph;
    private final V source;
    private final V sink;
    private PathsGraphVertex pathsGraphRoot;
    private Map<V, Pair<Double, E>> distanceAndPredecessorMap;
    private Queue<EppsteinGraphPath> pathsQueue;
    private Map<V, PathsGraphVertex> hMapping;

    public EppsteinShortestPathIterator(Graph<V, E> graph, V source, V sink) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null!");
        GraphType type = graph.getType();
        if (!type.isDirected() || !type.isSimple()) {
            throw new IllegalArgumentException("graph must be simple and directed");
        }
        if (!graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph does not contain source vertex");
        }
        this.source = source;
        if (!graph.containsVertex(sink)) {
            throw new IllegalArgumentException("Graph does not contain sink vertex");
        }
        this.sink = sink;
        this.pathsQueue = new PriorityQueue<EppsteinGraphPath>();
        TreeSingleSourcePathsImpl shortestPaths = (TreeSingleSourcePathsImpl)new DijkstraShortestPath<V, E>(new EdgeReversedGraph<V, E>(graph)).getPaths(sink);
        GraphPath shortestPath = shortestPaths.getPath(source);
        if (shortestPath != null) {
            this.distanceAndPredecessorMap = shortestPaths.getDistanceAndPredecessorMap();
            this.pathsQueue.add(new EppsteinGraphPath(graph, new ArrayList<PathsGraphVertex>(0), this.distanceAndPredecessorMap, shortestPath.getWeight()));
            this.hMapping = new HashMap<V, PathsGraphVertex>();
            this.buildPathsGraph();
        }
    }

    @Override
    public boolean hasNext() {
        return !this.pathsQueue.isEmpty();
    }

    @Override
    public GraphPath<V, E> next() {
        if (this.pathsQueue.isEmpty()) {
            throw new NoSuchElementException();
        }
        EppsteinGraphPath result = this.pathsQueue.remove();
        this.addOneEdgeExtension(result);
        return result;
    }

    private void addOneEdgeExtension(EppsteinGraphPath path) {
        PathsGraphVertex lastPathsGraphVertex = path.pathsGraphVertices.isEmpty() ? this.pathsGraphRoot : path.pathsGraphVertices.get(path.pathsGraphVertices.size() - 1);
        if (lastPathsGraphVertex.left != null) {
            this.addExtension(path, lastPathsGraphVertex.left, lastPathsGraphVertex.left.delta - lastPathsGraphVertex.delta);
        }
        if (lastPathsGraphVertex.right != null) {
            this.addExtension(path, lastPathsGraphVertex.right, lastPathsGraphVertex.right.delta - lastPathsGraphVertex.delta);
        }
        if (lastPathsGraphVertex.rest != null) {
            this.addExtension(path, lastPathsGraphVertex.rest, lastPathsGraphVertex.rest.delta - lastPathsGraphVertex.delta);
        }
        if (lastPathsGraphVertex.cross != null) {
            this.addExtension(path, lastPathsGraphVertex.cross, lastPathsGraphVertex.cross.delta);
        }
    }

    private void addExtension(EppsteinGraphPath path, PathsGraphVertex extendingVertex, double weight) {
        ArrayList<PathsGraphVertex> sidetracks = new ArrayList<PathsGraphVertex>(path.pathsGraphVertices);
        sidetracks.add(extendingVertex);
        this.pathsQueue.add(new EppsteinGraphPath(this.graph, sidetracks, this.distanceAndPredecessorMap, path.weight + weight));
    }

    private void buildPathsGraph() {
        this.buildDGraph();
        this.addCrossEdges();
        this.addPathGraphRoot();
    }

    private void buildDGraph() {
        DepthFirstIterator<V, E> it = new DepthFirstIterator<V, E>(this.graph, this.source);
        ArrayDeque<V> stack = new ArrayDeque<V>();
        while (it.hasNext()) {
            Object vertex = it.next();
            if (!this.distanceAndPredecessorMap.containsKey(vertex) || this.hMapping.containsKey(vertex)) continue;
            stack.addLast(vertex);
            while (!stack.isEmpty()) {
                Object v = stack.peekLast();
                if (v.equals(this.sink)) {
                    stack.removeLast();
                    this.insertVertex(v, null);
                    continue;
                }
                V predecessor = Graphs.getOppositeVertex(this.graph, this.distanceAndPredecessorMap.get(v).getSecond(), v);
                if (this.hMapping.containsKey(predecessor)) {
                    stack.removeLast();
                    PathsGraphVertex predecessorH = this.hMapping.get(predecessor);
                    this.insertVertex(v, predecessorH);
                    continue;
                }
                stack.addLast(predecessor);
            }
        }
    }

    private void addCrossEdges() {
        ArrayDeque<PathsGraphVertex> queue = new ArrayDeque<PathsGraphVertex>();
        PathsGraphVertex sourceMapping = this.hMapping.get(this.source);
        HashSet<PathsGraphVertex> seen = new HashSet<PathsGraphVertex>();
        if (sourceMapping != null) {
            queue.add(sourceMapping);
            while (!queue.isEmpty()) {
                PathsGraphVertex v = (PathsGraphVertex)queue.remove();
                seen.add(v);
                V target = this.graph.getEdgeTarget(v.edge);
                v.cross = this.hMapping.get(target);
                if (v.left != null && !seen.contains(v.left)) {
                    queue.add(v.left);
                }
                if (v.right != null && !seen.contains(v.right)) {
                    queue.add(v.right);
                }
                if (v.rest != null && !seen.contains(v.rest)) {
                    queue.add(v.rest);
                }
                if (v.cross == null || seen.contains(v.cross)) continue;
                queue.add(v.cross);
            }
        }
    }

    private void addPathGraphRoot() {
        PathsGraphVertex root = new PathsGraphVertex(null, 0.0);
        root.cross = this.hMapping.get(this.source);
        this.pathsGraphRoot = root;
    }

    private void insertVertex(V v, PathsGraphVertex predecessorHeap) {
        Pair<PathsGraphVertex, PathsGraphVertex> p = this.getOutrootAndRestHeapRoot(v);
        PathsGraphVertex outroot = p.getFirst();
        PathsGraphVertex restHeapRoot = p.getSecond();
        if (outroot == null) {
            this.hMapping.put((PathsGraphVertex)v, predecessorHeap);
        } else {
            PathsGraphVertex mappingVertex = this.insertPersistently(predecessorHeap, outroot);
            this.hMapping.put((PathsGraphVertex)v, mappingVertex);
            mappingVertex.rest = restHeapRoot;
        }
    }

    private PathsGraphVertex insertPersistently(PathsGraphVertex root, PathsGraphVertex vertex) {
        PathsGraphVertex max;
        PathsGraphVertex min;
        boolean leftDirection;
        if (root == null) {
            vertex.left = null;
            vertex.right = null;
            vertex.size = 1;
            return vertex;
        }
        PathsGraphVertex rootCopy = new PathsGraphVertex(root);
        boolean bl = leftDirection = root.left == null || root.right != null && root.left.size <= root.right.size;
        if (vertex.delta >= rootCopy.delta) {
            min = rootCopy;
            max = vertex;
        } else {
            vertex.left = rootCopy.left;
            vertex.right = rootCopy.right;
            vertex.size = rootCopy.size;
            rootCopy.left = null;
            rootCopy.right = null;
            min = vertex;
            max = rootCopy;
        }
        if (leftDirection) {
            min.left = this.insertPersistently(min.left, max);
        } else {
            min.right = this.insertPersistently(min.right, max);
        }
        ++min.size;
        return min;
    }

    private Pair<PathsGraphVertex, PathsGraphVertex> getOutrootAndRestHeapRoot(V v) {
        ArrayList<PathsGraphVertex> restHeapElements = new ArrayList<PathsGraphVertex>();
        PathsGraphVertex outroot = new PathsGraphVertex(null, Double.POSITIVE_INFINITY);
        E predecessor = this.distanceAndPredecessorMap.get(v).getSecond();
        for (E e : this.graph.outgoingEdgesOf(v)) {
            if (!this.distanceAndPredecessorMap.containsKey(this.graph.getEdgeTarget(e)) || e.equals(predecessor)) continue;
            double delta = this.delta(e);
            if (delta < outroot.delta) {
                if (outroot.edge != null) {
                    restHeapElements.add(outroot);
                }
                outroot = new PathsGraphVertex(e, delta);
                continue;
            }
            restHeapElements.add(new PathsGraphVertex(e, delta));
        }
        PathsGraphVertex restHeapRoot = null;
        int size = restHeapElements.size();
        if (size > 0) {
            this.heapify(restHeapElements, size);
            restHeapRoot = this.getRestHeap(restHeapElements, 0, size);
        }
        if (outroot.edge == null) {
            return new Pair<Object, Object>(null, restHeapRoot);
        }
        return new Pair<PathsGraphVertex, Object>(outroot, restHeapRoot);
    }

    private void heapify(List<PathsGraphVertex> vertices, int size) {
        for (int i = size / 2 - 1; i >= 0; --i) {
            this.siftDown(vertices, i, size);
        }
    }

    private void siftDown(List<PathsGraphVertex> vertices, int i, int size) {
        int current = i;
        while (true) {
            int left = 2 * current + 1;
            int right = 2 * current + 2;
            int smaller = current;
            if (left < size && vertices.get(left).compareTo(vertices.get(smaller)) < 0) {
                smaller = left;
            }
            if (right < size && vertices.get(right).compareTo(vertices.get(smaller)) < 0) {
                smaller = right;
            }
            if (smaller == current) break;
            this.swap(vertices, current, smaller);
            current = smaller;
        }
    }

    private PathsGraphVertex getRestHeap(List<PathsGraphVertex> vertices, int i, int size) {
        int l = 2 * i + 1;
        int r = 2 * i + 2;
        if (l < size) {
            vertices.get((int)i).left = this.getRestHeap(vertices, l, size);
        }
        if (r < size) {
            vertices.get((int)i).right = this.getRestHeap(vertices, r, size);
        }
        return vertices.get(i);
    }

    private void swap(List<PathsGraphVertex> vertices, int i, int j) {
        if (i != j) {
            PathsGraphVertex tmp = vertices.get(i);
            vertices.set(i, vertices.get(j));
            vertices.set(j, tmp);
        }
    }

    private double delta(E e) {
        return this.graph.getEdgeWeight(e) + this.distanceAndPredecessorMap.get(this.graph.getEdgeTarget(e)).getFirst() - this.distanceAndPredecessorMap.get(this.graph.getEdgeSource(e)).getFirst();
    }

    private class EppsteinGraphPath
    implements GraphPath<V, E>,
    Comparable<EppsteinGraphPath> {
        private Graph<V, E> graph;
        private List<PathsGraphVertex> pathsGraphVertices;
        private Map<V, Pair<Double, E>> distanceAndPredecessorMap;
        private double weight;

        EppsteinGraphPath(Graph<V, E> graph, List<PathsGraphVertex> pathsGraphVertices, Map<V, Pair<Double, E>> distanceAndPredecessorMap, double weight) {
            this.graph = graph;
            this.pathsGraphVertices = pathsGraphVertices;
            this.distanceAndPredecessorMap = distanceAndPredecessorMap;
            this.weight = weight;
        }

        @Override
        public Graph<V, E> getGraph() {
            return this.graph;
        }

        @Override
        public V getStartVertex() {
            return EppsteinShortestPathIterator.this.source;
        }

        @Override
        public V getEndVertex() {
            return EppsteinShortestPathIterator.this.sink;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        @Override
        public List<E> getEdgeList() {
            List<PathsGraphVertex> sidetracks = this.getSidetracks(this.pathsGraphVertices);
            ArrayList result = new ArrayList();
            Iterator<PathsGraphVertex> it = sidetracks.iterator();
            Object shortestPathSource = EppsteinShortestPathIterator.this.source;
            PathsGraphVertex sidetrack = null;
            if (it.hasNext()) {
                sidetrack = it.next();
            }
            while (sidetrack != null) {
                Object sidetrackSource = this.graph.getEdgeSource(sidetrack.edge);
                while (!shortestPathSource.equals(sidetrackSource)) {
                    Object shortestPathEdge = this.distanceAndPredecessorMap.get(shortestPathSource).getSecond();
                    result.add(shortestPathEdge);
                    shortestPathSource = Graphs.getOppositeVertex(this.graph, shortestPathEdge, shortestPathSource);
                }
                PathsGraphVertex curr = sidetrack;
                PathsGraphVertex next = null;
                while (it.hasNext()) {
                    next = it.next();
                    if (!this.graph.getEdgeTarget(curr.edge).equals(this.graph.getEdgeSource(next.edge))) break;
                    result.add(curr.edge);
                    curr = next;
                    next = null;
                }
                result.add(curr.edge);
                sidetrack = next;
                shortestPathSource = this.graph.getEdgeTarget(curr.edge);
            }
            while (!shortestPathSource.equals(EppsteinShortestPathIterator.this.sink)) {
                Object edge = this.distanceAndPredecessorMap.get(shortestPathSource).getSecond();
                result.add(edge);
                shortestPathSource = this.graph.getEdgeTarget(edge);
            }
            return result;
        }

        private List<PathsGraphVertex> getSidetracks(List<PathsGraphVertex> vertices) {
            if (vertices.size() > 1) {
                ArrayList<Integer> toBeRemoved = new ArrayList<Integer>();
                Iterator<PathsGraphVertex> it = vertices.iterator();
                PathsGraphVertex curr = it.next();
                int currPosition = 0;
                while (it.hasNext()) {
                    PathsGraphVertex next = it.next();
                    if (curr.left == next || curr.right == next || curr.rest == next) {
                        toBeRemoved.add(currPosition);
                    }
                    curr = next;
                    ++currPosition;
                }
                ArrayList<PathsGraphVertex> result = new ArrayList<PathsGraphVertex>(vertices.size() - toBeRemoved.size());
                int size = toBeRemoved.size();
                int j = 0;
                for (int i = 0; i < vertices.size(); ++i) {
                    if (j < size && ((Integer)toBeRemoved.get(j)).equals(i)) {
                        ++j;
                        continue;
                    }
                    result.add(vertices.get(i));
                }
                return result;
            }
            return vertices;
        }

        @Override
        public int compareTo(EppsteinGraphPath o) {
            return Double.compare(this.weight, o.weight);
        }
    }

    private class PathsGraphVertex
    implements Comparable<PathsGraphVertex> {
        E edge;
        double delta;
        int size;
        PathsGraphVertex left;
        PathsGraphVertex right;
        PathsGraphVertex rest;
        PathsGraphVertex cross;

        PathsGraphVertex(E edge, double delta) {
            this.edge = edge;
            this.delta = delta;
            this.size = 1;
        }

        PathsGraphVertex(PathsGraphVertex other) {
            this.edge = other.edge;
            this.size = other.size;
            this.delta = other.delta;
            this.left = other.left;
            this.right = other.right;
            this.cross = other.cross;
            this.rest = other.rest;
        }

        @Override
        public int compareTo(PathsGraphVertex o) {
            return Double.compare(this.delta, o.delta);
        }
    }
}

