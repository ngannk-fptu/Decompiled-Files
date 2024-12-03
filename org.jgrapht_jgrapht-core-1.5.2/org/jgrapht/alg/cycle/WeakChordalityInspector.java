/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.cycle;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.generate.ComplementGraphGenerator;
import org.jgrapht.graph.AsUndirectedGraph;
import org.jgrapht.graph.GraphWalk;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.util.CollectionUtil;
import org.jgrapht.util.VertexToIntegerMapping;

public class WeakChordalityInspector<V, E> {
    private final int n;
    private final int m;
    private Graph<V, E> graph;
    private Map<V, Integer> vertices;
    private List<V> indices;
    private Boolean weaklyChordal = null;
    private GraphPath<V, E> certificate;

    public WeakChordalityInspector(Graph<V, E> graph) {
        this.graph = Objects.requireNonNull(graph);
        if (graph.getType().isDirected()) {
            this.graph = new AsUndirectedGraph<V, E>(graph);
        }
        this.n = graph.vertexSet().size();
        this.m = graph.edgeSet().size();
        this.initMappings();
    }

    private void initMappings() {
        VertexToIntegerMapping<V> mapping = new VertexToIntegerMapping<V>(this.graph.vertexSet());
        this.vertices = mapping.getVertexMap();
        this.indices = mapping.getIndexList();
    }

    public boolean isWeaklyChordal() {
        return this.lazyComputeWeakChordality();
    }

    public GraphPath<V, E> getCertificate() {
        this.lazyComputeWeakChordality();
        return this.certificate;
    }

    private boolean lazyComputeWeakChordality() {
        if (this.weaklyChordal == null) {
            List<Pair<List<Pair<Integer, Integer>>, E>> globalSeparatorList = this.computeGlobalSeparatorList();
            if (globalSeparatorList.size() > 0) {
                this.sortSeparatorsList(globalSeparatorList);
                int separatorsNum = 1;
                List<Pair<Integer, Integer>> original = globalSeparatorList.get(0).getFirst();
                List<List<Integer>> coConnectedComponents = this.computeCoConnectedComponents(this.graph, original);
                for (Pair<List<Pair<Integer, Integer>>, E> separator : globalSeparatorList) {
                    Pair<Integer, Integer> pair;
                    if (this.unequalSeparators(original, separator.getFirst())) {
                        original = separator.getFirst();
                        if (this.n + this.m < ++separatorsNum) {
                            this.weaklyChordal = false;
                            return this.weaklyChordal;
                        }
                        coConnectedComponents = this.computeCoConnectedComponents(this.graph, original);
                    }
                    if ((pair = this.checkLabels(coConnectedComponents, separator.getFirst())) == null) continue;
                    E holeFormer = separator.getSecond();
                    V source = this.graph.getEdgeSource(holeFormer);
                    V target = this.graph.getEdgeTarget(holeFormer);
                    V sourceInSeparator = this.indices.get(pair.getFirst());
                    V targetInSeparator = this.indices.get(pair.getSecond());
                    if (!this.graph.containsEdge(source, sourceInSeparator)) {
                        V t = sourceInSeparator;
                        sourceInSeparator = targetInSeparator;
                        targetInSeparator = t;
                    }
                    if (this.graph.containsEdge(sourceInSeparator, targetInSeparator)) {
                        this.findAntiHole(source, targetInSeparator);
                    } else {
                        this.findHole(sourceInSeparator, source, target, targetInSeparator);
                    }
                    this.weaklyChordal = false;
                    return this.weaklyChordal;
                }
                this.weaklyChordal = true;
                return this.weaklyChordal;
            }
            this.weaklyChordal = true;
            return this.weaklyChordal;
        }
        return this.weaklyChordal;
    }

    private List<Pair<List<Pair<Integer, Integer>>, E>> computeGlobalSeparatorList() {
        ArrayList<Pair<List<Pair<Integer, Integer>>, Pair<List<Pair<Integer, Integer>>, E>>> globalSeparatorList = new ArrayList<Pair<List<Pair<Integer, Integer>>, Pair<List<Pair<Integer, Integer>>, E>>>();
        for (E edge : this.graph.edgeSet()) {
            V target;
            V source = this.graph.getEdgeSource(edge);
            if (source == (target = this.graph.getEdgeTarget(edge))) continue;
            List<Set<V>> edgeSeparators = this.findSeparators(this.graph, edge);
            globalSeparatorList.addAll(this.reformatSeparatorList(edgeSeparators, edge));
        }
        return globalSeparatorList;
    }

    private List<Pair<List<Pair<Integer, Integer>>, E>> reformatSeparatorList(List<Set<V>> separators, E edge) {
        List<Integer> labeling = this.getLabeling(edge);
        ArrayList reformattedSeparators = new ArrayList();
        ArrayList vInSeparator = new ArrayList(this.n);
        for (int i = 0; i < this.n; ++i) {
            vInSeparator.add(new ArrayList());
        }
        for (Set<V> computedSeparator : separators) {
            ArrayList reformattedSeparator = new ArrayList(computedSeparator.size());
            reformattedSeparators.add(new Pair(reformattedSeparator, edge));
            for (V vertex : computedSeparator) {
                int vertexIndex = this.vertices.get(vertex);
                ((List)vInSeparator.get(vertexIndex)).add(reformattedSeparator);
            }
        }
        for (int vertex = 0; vertex < this.n; ++vertex) {
            List listOfSeparators = (List)vInSeparator.get(vertex);
            for (List separator : listOfSeparators) {
                separator.add(new Pair<Integer, Integer>(vertex, labeling.get(vertex)));
            }
        }
        return reformattedSeparators;
    }

    private List<Integer> getLabeling(E edge) {
        V source = this.graph.getEdgeSource(edge);
        V target = this.graph.getEdgeTarget(edge);
        ArrayList<Object> labeling = new ArrayList<Object>(Collections.nCopies(this.n, null));
        for (E sourceEdge : this.graph.edgesOf(source)) {
            labeling.set(this.vertices.get(Graphs.getOppositeVertex(this.graph, sourceEdge, source)), 1);
        }
        for (E targetEdge : this.graph.edgesOf(target)) {
            Integer oppositeIndex = this.vertices.get(Graphs.getOppositeVertex(this.graph, targetEdge, target));
            if (labeling.get(oppositeIndex) != null) {
                labeling.set(oppositeIndex, 3);
                continue;
            }
            labeling.set(oppositeIndex, 2);
        }
        return labeling;
    }

    /*
     * WARNING - void declaration
     */
    private void sortSeparatorsList(List<Pair<List<Pair<Integer, Integer>>, E>> separators) {
        void var5_9;
        void var5_7;
        ArrayDeque<Pair<List<Pair<Integer, Integer>>, Pair>> mainQueue = new ArrayDeque<Pair<List<Pair<Integer, Integer>>, Pair>>();
        int maxSeparatorLength = 0;
        for (Pair<List<Pair<Integer, Integer>>, Pair> pair : separators) {
            if (pair.getFirst().size() > maxSeparatorLength) {
                maxSeparatorLength = pair.getFirst().size();
            }
            mainQueue.add(pair);
        }
        separators.clear();
        ArrayList queues = new ArrayList(this.n);
        boolean bl = false;
        while (var5_7 < this.n) {
            queues.add(new LinkedList());
            ++var5_7;
        }
        boolean bl2 = false;
        while (var5_9 < maxSeparatorLength) {
            while (!mainQueue.isEmpty()) {
                Pair separator = (Pair)mainQueue.remove();
                if (var5_9 >= ((List)separator.getFirst()).size()) {
                    separators.add(separator);
                    continue;
                }
                ((Queue)queues.get((Integer)((Pair)((List)separator.getFirst()).get(((List)separator.getFirst()).size() - var5_9 - 1)).getFirst())).add(separator);
            }
            for (Queue queue : queues) {
                mainQueue.addAll(queue);
                queue.clear();
            }
            ++var5_9;
        }
        separators.addAll(mainQueue);
    }

    private boolean unequalSeparators(List<Pair<Integer, Integer>> sep1, List<Pair<Integer, Integer>> sep2) {
        if (sep1.size() == sep2.size()) {
            for (int i = 0; i < sep1.size(); ++i) {
                if (sep2.get(i).getFirst().equals(sep1.get(i).getFirst())) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    private List<List<Integer>> computeCoConnectedComponents(Graph<V, E> graph, List<Pair<Integer, Integer>> separator) {
        ArrayList<List<Integer>> coConnectedComponents = new ArrayList<List<Integer>>();
        ArrayList<Set<Integer>> bucketsByLabel = new ArrayList<Set<Integer>>(separator.size());
        for (int i = 0; i < separator.size(); ++i) {
            bucketsByLabel.add(new HashSet());
        }
        ArrayList<Integer> labels = new ArrayList<Integer>(Collections.nCopies(this.n, -1));
        HashSet unvisited = CollectionUtil.newHashSetWithExpectedSize(separator.size());
        separator.forEach(pair -> {
            unvisited.add((Integer)pair.getFirst());
            labels.set((Integer)pair.getFirst(), 0);
        });
        bucketsByLabel.set(0, unvisited);
        int minLabel = 0;
        while (unvisited.size() > 0) {
            ArrayList<Integer> coConnectedComponent = new ArrayList<Integer>();
            block2: while (true) {
                if (!((Set)bucketsByLabel.get(minLabel)).isEmpty()) {
                    Integer vertex = (Integer)((Set)bucketsByLabel.get(minLabel)).iterator().next();
                    ((Set)bucketsByLabel.get(minLabel)).remove(vertex);
                    coConnectedComponent.add(vertex);
                    labels.set(vertex, -1);
                    Iterator<E> iterator = graph.edgesOf(this.indices.get(vertex)).iterator();
                    while (true) {
                        if (!iterator.hasNext()) continue block2;
                        E edge = iterator.next();
                        Integer opposite = this.vertices.get(Graphs.getOppositeVertex(graph, edge, this.indices.get(vertex)));
                        Integer oppositeLabel = (Integer)labels.get(opposite);
                        if (oppositeLabel == -1) continue;
                        this.putToNextBucket(opposite, oppositeLabel, bucketsByLabel, labels);
                    }
                }
                if (++minLabel == coConnectedComponent.size()) break;
            }
            this.reload(bucketsByLabel, labels, minLabel);
            coConnectedComponents.add(coConnectedComponent);
            minLabel = 0;
        }
        return coConnectedComponents;
    }

    private void putToNextBucket(Integer vertex, Integer vertexLabel, List<Set<Integer>> bucketsByLabel, List<Integer> labels) {
        bucketsByLabel.get(vertexLabel).remove(vertex);
        bucketsByLabel.get(vertexLabel + 1).add(vertex);
        labels.set(vertex, vertexLabel + 1);
    }

    private void reload(List<Set<Integer>> bucketsByLabel, List<Integer> labels, int minLabel) {
        if (minLabel != 0 && minLabel < bucketsByLabel.size()) {
            Set<Integer> bucket = bucketsByLabel.get(minLabel);
            for (Integer vertex : bucket) {
                labels.set(vertex, 0);
                bucketsByLabel.get(0).add(vertex);
            }
            bucket.clear();
        }
    }

    private Pair<Integer, Integer> checkLabels(List<List<Integer>> coConnectedComponents, List<Pair<Integer, Integer>> separator) {
        ArrayList<Object> vertexLabels = new ArrayList<Object>(Collections.nCopies(this.n, null));
        for (Pair<Integer, Integer> pair : separator) {
            vertexLabels.set(pair.getFirst(), pair.getSecond());
        }
        for (List list : coConnectedComponents) {
            int label = 0;
            Integer labelVertex = null;
            for (Integer vertex : list) {
                if ((Integer)vertexLabels.get(vertex) == 3) continue;
                if (label != 0) {
                    if (label == (Integer)vertexLabels.get(vertex)) continue;
                    return new Pair<Integer, Integer>(labelVertex, vertex);
                }
                label = (Integer)vertexLabels.get(vertex);
                labelVertex = vertex;
            }
        }
        return null;
    }

    private void findHole(V sourceInSeparator, V source, V target, V targetInSeparator) {
        this.certificate = this.findHole(this.graph, sourceInSeparator, source, target, targetInSeparator);
    }

    private void findAntiHole(V source, V targetInSeparator) {
        ComplementGraphGenerator<V, E> generator = new ComplementGraphGenerator<V, E>(this.graph, false);
        Object complement = Pseudograph.createBuilder(this.graph.getEdgeSupplier()).build();
        generator.generateGraph((Graph<V, E>)complement);
        Object cycleFormer = complement.getEdge(source, targetInSeparator);
        V cycleSource = this.graph.getEdgeSource(cycleFormer);
        V cycleTarget = this.graph.getEdgeTarget(cycleFormer);
        List<Set<V>> separators = this.findSeparators((Graph<V, E>)complement, cycleFormer);
        List reformatted = this.reformatSeparatorList(separators, cycleFormer);
        this.sortSeparatorsList(reformatted);
        List<Pair<Integer, Integer>> original = reformatted.get(0).getFirst();
        List<List<Integer>> coConnectedComponents = this.computeCoConnectedComponents((Graph<V, E>)complement, original);
        for (Pair separator : reformatted) {
            Pair<Integer, Integer> pair;
            if (this.unequalSeparators(separator.getFirst(), original)) {
                original = separator.getFirst();
                coConnectedComponents = this.computeCoConnectedComponents((Graph<V, E>)complement, separator.getFirst());
            }
            if ((pair = this.checkLabels(coConnectedComponents, separator.getFirst())) == null) continue;
            V cycleSourceInSeparator = this.indices.get(pair.getFirst());
            V cycleTargetInSeparator = this.indices.get(pair.getSecond());
            if (!complement.containsEdge(cycleSourceInSeparator, cycleSource)) {
                V t = cycleSourceInSeparator;
                cycleSourceInSeparator = cycleTargetInSeparator;
                cycleTargetInSeparator = t;
            }
            this.certificate = this.findHole((Graph<V, E>)complement, cycleSourceInSeparator, cycleSource, cycleTarget, cycleTargetInSeparator);
            return;
        }
    }

    private GraphPath<V, E> findHole(Graph<V, E> graph, V sourceInSeparator, V source, V target, V targetInSeparator) {
        HashSet<V> visited = CollectionUtil.newHashSetWithExpectedSize(graph.vertexSet().size());
        visited.add(target);
        visited.add(source);
        List<Object> cycle = this.findCycle(visited, graph, targetInSeparator, target, source, sourceInSeparator);
        cycle = this.minimizeCycle(graph, cycle, target, targetInSeparator, source, sourceInSeparator);
        return new GraphWalk<V, E>(graph, cycle, 0.0);
    }

    private List<V> findCycle(Set<V> visited, Graph<V, E> graph, V tarInSep, V tar, V sour, V sourInSep) {
        ArrayList<Object> cycle = new ArrayList<Object>(Arrays.asList(tarInSep, tar, sour));
        ArrayDeque<V> stack = new ArrayDeque<V>();
        stack.add(sourInSep);
        while (!stack.isEmpty()) {
            Object currentVertex = stack.removeLast();
            if (!visited.add(currentVertex)) continue;
            while (!graph.containsEdge(cycle.get(cycle.size() - 1), currentVertex)) {
                cycle.remove(cycle.size() - 1);
            }
            cycle.add(currentVertex);
            if (tarInSep.equals(currentVertex)) break;
            for (V neighbor : Graphs.neighborListOf(graph, currentVertex)) {
                if (visited.contains(neighbor) || graph.containsEdge(sour, neighbor) || graph.containsEdge(tar, neighbor) && !neighbor.equals(tarInSep)) continue;
                stack.add(neighbor);
            }
        }
        return cycle;
    }

    private List<V> minimizeCycle(Graph<V, E> graph, List<V> cycle, V tar, V tarInSep, V sour, V sourInSep) {
        ArrayList<Object> minimizedCycle = new ArrayList<Object>(Arrays.asList(tarInSep, tar, sour));
        HashSet<V> forwardVertices = new HashSet<V>(cycle);
        forwardVertices.remove(tar);
        forwardVertices.remove(sour);
        forwardVertices.remove(sourInSep);
        int i = 3;
        while (i < cycle.size() - 1) {
            V current = cycle.get(i);
            minimizedCycle.add(current);
            forwardVertices.remove(current);
            HashSet<V> currentForward = new HashSet<V>();
            for (V neighbor : Graphs.neighborListOf(graph, current)) {
                if (!forwardVertices.contains(neighbor)) continue;
                currentForward.add(neighbor);
            }
            for (Object forwardVertex : currentForward) {
                if (!forwardVertices.contains(forwardVertex)) continue;
                do {
                    forwardVertices.remove(cycle.get(i));
                } while (++i < cycle.size() && !cycle.get(i).equals(forwardVertex));
            }
        }
        minimizedCycle.add(tarInSep);
        return minimizedCycle;
    }

    private List<Set<V>> findSeparators(Graph<V, E> graph, E edge) {
        ArrayList<Set<V>> separators = new ArrayList<Set<V>>();
        V source = graph.getEdgeSource(edge);
        V target = graph.getEdgeTarget(edge);
        Set<V> neighborhood = this.neighborhoodSetOf(graph, edge);
        HashMap<V, Byte> dfsMap = CollectionUtil.newHashMapWithExpectedSize(graph.vertexSet().size());
        for (V vertex : graph.vertexSet()) {
            if (neighborhood.contains(vertex)) {
                dfsMap.put(vertex, (byte)1);
                continue;
            }
            dfsMap.put(vertex, (byte)0);
        }
        dfsMap.put(source, (byte)2);
        dfsMap.put(target, (byte)2);
        for (V vertex : graph.vertexSet()) {
            Set<V> separator;
            if ((Byte)dfsMap.get(vertex) != 0 || (separator = this.getSeparator(graph, vertex, dfsMap)).isEmpty()) continue;
            separators.add(separator);
        }
        return separators;
    }

    private Set<V> getSeparator(Graph<V, E> graph, V startVertex, Map<V, Byte> dfsMap) {
        ArrayDeque<V> stack = new ArrayDeque<V>();
        HashSet<V> separator = new HashSet<V>();
        stack.add(startVertex);
        while (!stack.isEmpty()) {
            Object currentVertex = stack.removeLast();
            if (dfsMap.get(currentVertex) != 0) continue;
            dfsMap.put((Byte)currentVertex, (byte)2);
            for (E edge : graph.edgesOf(currentVertex)) {
                V opposite = Graphs.getOppositeVertex(graph, edge, currentVertex);
                if (dfsMap.get(opposite) == 0) {
                    stack.add(opposite);
                    continue;
                }
                if (dfsMap.get(opposite) != 1) continue;
                separator.add(opposite);
            }
        }
        return separator;
    }

    private Set<V> neighborhoodSetOf(Graph<V, E> g, E edge) {
        HashSet<V> neighborhood = new HashSet<V>();
        V source = g.getEdgeSource(edge);
        V target = g.getEdgeTarget(edge);
        for (E e : g.edgesOf(source)) {
            neighborhood.add(Graphs.getOppositeVertex(g, e, source));
        }
        for (E e : g.edgesOf(target)) {
            neighborhood.add(Graphs.getOppositeVertex(g, e, target));
        }
        neighborhood.remove(source);
        neighborhood.remove(target);
        return neighborhood;
    }
}

