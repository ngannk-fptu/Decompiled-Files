/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jheaps.Heap
 *  org.jheaps.array.DaryArrayHeap
 */
package org.jgrapht.alg.shortestpath;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.MultiObjectiveShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.BaseMultiObjectiveShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.ListMultiObjectiveSingleSourcePathsImpl;
import org.jgrapht.graph.GraphWalk;
import org.jheaps.Heap;
import org.jheaps.array.DaryArrayHeap;

public class MartinShortestPath<V, E>
extends BaseMultiObjectiveShortestPathAlgorithm<V, E> {
    private final Function<E, double[]> edgeWeightFunction;
    private final int objectives;
    private final Map<V, LinkedList<Label>> nodeLabels;
    private final Heap<Label> heap;

    public MartinShortestPath(Graph<V, E> graph, Function<E, double[]> edgeWeightFunction) {
        super(graph);
        this.edgeWeightFunction = Objects.requireNonNull(edgeWeightFunction, "Function cannot be null");
        this.objectives = this.validateEdgeWeightFunction(edgeWeightFunction);
        this.nodeLabels = new HashMap<V, LinkedList<Label>>();
        this.heap = new DaryArrayHeap(3, (Comparator)new LabelComparator());
    }

    @Override
    public List<GraphPath<V, E>> getPaths(V source, V sink) {
        return this.getPaths(source).getPaths(sink);
    }

    @Override
    public MultiObjectiveShortestPathAlgorithm.MultiObjectiveSingleSourcePaths<V, E> getPaths(V source) {
        if (!this.graph.containsVertex(source)) {
            throw new IllegalArgumentException("Graph must contain the source vertex!");
        }
        if (this.graph.vertexSet().isEmpty() || this.graph.edgeSet().isEmpty()) {
            return new ListMultiObjectiveSingleSourcePathsImpl(this.graph, source, Collections.emptyMap());
        }
        if (this.nodeLabels.isEmpty()) {
            this.runAlgorithm(source);
        }
        Map<V, List<GraphPath<V, E>>> paths = this.buildPaths(source);
        return new ListMultiObjectiveSingleSourcePathsImpl<V, E>(this.graph, source, paths);
    }

    private void runAlgorithm(V source) {
        Label sourceLabel = new Label(source, new double[this.objectives], null, null);
        for (Object v : this.graph.vertexSet()) {
            this.nodeLabels.put(v, (LinkedList<Label>)new LinkedList());
        }
        this.nodeLabels.get(source).add(sourceLabel);
        this.heap.insert((Object)sourceLabel);
        while (!this.heap.isEmpty()) {
            Object v;
            Label curLabel = (Label)this.heap.deleteMin();
            v = curLabel.node;
            for (Object e : this.graph.outgoingEdgesOf(v)) {
                Object u = Graphs.getOppositeVertex(this.graph, e, v);
                Label newLabel = new Label(u, MartinShortestPath.sum(curLabel.value, this.edgeWeightFunction.apply(e)), curLabel, e);
                boolean isDominated = false;
                LinkedList<Label> uLabels = this.nodeLabels.get(u);
                ListIterator it = uLabels.listIterator();
                while (it.hasNext()) {
                    Label oldLabel = (Label)it.next();
                    if (MartinShortestPath.dominates(oldLabel.value, newLabel.value)) {
                        isDominated = true;
                        break;
                    }
                    if (!MartinShortestPath.dominates(newLabel.value, oldLabel.value)) continue;
                    it.remove();
                }
                if (isDominated) continue;
                uLabels.add(newLabel);
                this.heap.insert((Object)newLabel);
            }
        }
    }

    private Map<V, List<GraphPath<V, E>>> buildPaths(V source) {
        HashMap paths = new HashMap();
        for (Object sink : this.graph.vertexSet()) {
            if (sink.equals(source)) {
                paths.put(sink, Arrays.asList(this.createEmptyPath(source, sink)));
                continue;
            }
            paths.put(sink, this.nodeLabels.get(sink).stream().map(l -> {
                double weight = 0.0;
                LinkedList edgeList = new LinkedList();
                Label cur = l;
                while (cur != null && cur.fromPrevious != null) {
                    weight += this.graph.getEdgeWeight(cur.fromPrevious);
                    edgeList.push(cur.fromPrevious);
                    cur = cur.previous;
                }
                return new GraphWalk(this.graph, source, sink, edgeList, weight);
            }).collect(Collectors.toList()));
        }
        return paths;
    }

    private static double[] sum(double[] a, double[] b) {
        int d = a.length;
        double[] res = new double[d];
        for (int i = 0; i < d; ++i) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    private static boolean dominates(double[] a, double[] b) {
        boolean strict = false;
        int d = a.length;
        for (int i = 0; i < d; ++i) {
            if (a[i] > b[i]) {
                return false;
            }
            if (!(a[i] < b[i])) continue;
            strict = true;
        }
        return strict;
    }

    private int validateEdgeWeightFunction(Function<E, double[]> edgeWeightFunction) {
        int dim = 0;
        for (Object e : this.graph.edgeSet()) {
            double[] f = edgeWeightFunction.apply(e);
            if (f == null) {
                throw new IllegalArgumentException("Invalid edge weight function");
            }
            if (dim == 0) {
                dim = f.length;
            } else if (dim != f.length) {
                throw new IllegalArgumentException("Invalid edge weight function");
            }
            for (int i = 0; i < dim; ++i) {
                if (Double.compare(f[i], 0.0) >= 0) continue;
                throw new IllegalArgumentException("Edge weight must be non-negative");
            }
        }
        return dim;
    }

    private class LabelComparator
    implements Comparator<Label> {
        private LabelComparator() {
        }

        @Override
        public int compare(Label o1, Label o2) {
            for (int i = 0; i < MartinShortestPath.this.objectives; ++i) {
                if (o1.value[i] < o2.value[i]) {
                    return -1;
                }
                if (!(o1.value[i] > o2.value[i])) continue;
                return 1;
            }
            return 0;
        }
    }

    private class Label {
        public V node;
        public double[] value;
        public Label previous;
        public E fromPrevious;

        public Label(V node, double[] value, Label previous, E fromPrevious) {
            this.node = node;
            this.value = value;
            this.previous = previous;
            this.fromPrevious = fromPrevious;
        }

        public String toString() {
            return "Label [node=" + this.node + ", value=" + Arrays.toString(this.value) + ", fromPrevious=" + this.fromPrevious + "]";
        }
    }
}

