/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.color;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm;
import org.jgrapht.util.CollectionUtil;

public class ColorRefinementAlgorithm<V, E>
implements VertexColoringAlgorithm<V> {
    private final Graph<V, E> graph;
    private final VertexColoringAlgorithm.Coloring<V> alpha;

    public ColorRefinementAlgorithm(Graph<V, E> graph, VertexColoringAlgorithm.Coloring<V> alpha) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.alpha = Objects.requireNonNull(alpha, "alpha cannot be null");
        if (!this.isAlphaConsistent(alpha, graph)) {
            throw new IllegalArgumentException("alpha is not a valid surjective l-coloring for the given graph.");
        }
    }

    public ColorRefinementAlgorithm(Graph<V, E> graph) {
        this(graph, ColorRefinementAlgorithm.getDefaultAlpha(graph.vertexSet()));
    }

    @Override
    public VertexColoringAlgorithm.Coloring<V> getColoring() {
        ColoringRepresentation rep = new ColoringRepresentation(this.graph, this.alpha);
        Deque<Integer> refineStack = this.getSortedStack(this.alpha);
        while (!refineStack.isEmpty()) {
            Integer currentColor = refineStack.pop();
            Set<Integer> adjacentColors = this.calculateColorDegrees(currentColor, rep);
            adjacentColors.stream().filter(c -> rep.minColorDegree[c] < rep.maxColorDegree[c]).sorted(Comparator.comparingInt(o -> o)).forEach(color -> this.splitUpColor((Integer)color, refineStack, rep));
            this.cleanupColorDegrees(adjacentColors, rep);
        }
        return new VertexColoringAlgorithm.ColoringImpl(rep.coloring, rep.coloring.size());
    }

    private Set<Integer> calculateColorDegrees(int refiningColor, ColoringRepresentation rep) {
        int n = this.graph.vertexSet().size();
        LinkedHashSet<Integer> adjacentColors = CollectionUtil.newLinkedHashSetWithExpectedSize(n);
        for (Object v : rep.colorClasses.get(refiningColor)) {
            Set inNeighborhood = this.graph.incomingEdgesOf(v).stream().map(e -> Graphs.getOppositeVertex(this.graph, e, v)).collect(Collectors.toSet());
            for (Object w : inNeighborhood) {
                rep.colorDegree.put((Integer)w, rep.colorDegree.get(w) + 1);
                if (rep.colorDegree.get(w) == 1) {
                    rep.positiveDegreeColorClasses.get(rep.coloring.get(w)).add(w);
                }
                adjacentColors.add(rep.coloring.get(w));
                if (rep.colorDegree.get(w) <= rep.maxColorDegree[rep.coloring.get(w)]) continue;
                rep.maxColorDegree[rep.coloring.get(w).intValue()] = rep.colorDegree.get(w);
            }
        }
        for (Integer c : adjacentColors) {
            if (rep.colorClasses.get(c).size() != rep.positiveDegreeColorClasses.get(c).size()) {
                rep.minColorDegree[c.intValue()] = 0;
                continue;
            }
            rep.minColorDegree[c.intValue()] = rep.maxColorDegree[c];
            for (Object v : rep.positiveDegreeColorClasses.get(c)) {
                if (rep.colorDegree.get(v) >= rep.minColorDegree[c]) continue;
                rep.minColorDegree[c.intValue()] = rep.colorDegree.get(v);
            }
        }
        return adjacentColors;
    }

    private void cleanupColorDegrees(Set<Integer> adjacentColors, ColoringRepresentation rep) {
        for (int c : adjacentColors) {
            for (Object v : rep.positiveDegreeColorClasses.get(c)) {
                rep.colorDegree.put((Integer)v, 0);
            }
            rep.maxColorDegree[c] = 0;
            rep.positiveDegreeColorClasses.set(c, new ArrayList());
        }
    }

    private void splitUpColor(Integer color, Deque<Integer> refineStack, ColoringRepresentation rep) {
        List positiveDegreeColorClasses = rep.positiveDegreeColorClasses.get(color);
        int maxColorDegree = rep.maxColorDegree[color];
        int[] numColorDegree = new int[maxColorDegree + 1];
        numColorDegree[0] = rep.colorClasses.get(color).size() - positiveDegreeColorClasses.size();
        for (Object v : positiveDegreeColorClasses) {
            int degree;
            int n = degree = rep.colorDegree.get(v).intValue();
            numColorDegree[n] = numColorDegree[n] + 1;
        }
        int maxColorDegreeIndex = 0;
        for (int i = 1; i <= maxColorDegree; ++i) {
            if (numColorDegree[i] <= numColorDegree[maxColorDegreeIndex]) continue;
            maxColorDegreeIndex = i;
        }
        int[] newMapping = new int[maxColorDegree + 1];
        boolean isCurrentColorInStack = refineStack.contains(color);
        for (int i = 0; i <= maxColorDegree; ++i) {
            if (numColorDegree[i] < 1) continue;
            if (i == rep.minColorDegree[color]) {
                newMapping[i] = color;
                if (isCurrentColorInStack || maxColorDegreeIndex == i) continue;
                refineStack.push(newMapping[i]);
                continue;
            }
            newMapping[i] = ++rep.lastUsedColor;
            if (!isCurrentColorInStack && i == maxColorDegreeIndex) continue;
            refineStack.push(newMapping[i]);
        }
        for (Object v : positiveDegreeColorClasses) {
            int value = newMapping[rep.colorDegree.get(v)];
            if (value == color) continue;
            rep.colorClasses.get(color).remove(v);
            rep.colorClasses.get(value).add(v);
            rep.coloring.replace((Integer)v, value);
        }
    }

    private boolean isAlphaConsistent(VertexColoringAlgorithm.Coloring<V> alpha, Graph<V, E> graph) {
        if (alpha.getColors().size() != graph.vertexSet().size()) {
            return false;
        }
        if (alpha.getColorClasses().size() != alpha.getNumberColors()) {
            return false;
        }
        for (V v : graph.vertexSet()) {
            if (!alpha.getColors().containsKey(v)) {
                return false;
            }
            Integer currentColor = alpha.getColors().get(v);
            if (currentColor + 1 <= alpha.getNumberColors() && currentColor >= 0) continue;
            return false;
        }
        return true;
    }

    private static <V> VertexColoringAlgorithm.Coloring<V> getDefaultAlpha(Set<V> vertices) {
        HashMap<V, Integer> alpha = new HashMap<V, Integer>();
        for (V v : vertices) {
            alpha.put(v, 0);
        }
        return new VertexColoringAlgorithm.ColoringImpl(alpha, 1);
    }

    private Deque<Integer> getSortedStack(VertexColoringAlgorithm.Coloring<V> alpha) {
        int numberColors = alpha.getNumberColors();
        ArrayDeque<Integer> stack = new ArrayDeque<Integer>(this.graph.vertexSet().size());
        for (int i = numberColors - 1; i >= 0; --i) {
            stack.push(i);
        }
        return stack;
    }

    private class ColoringRepresentation {
        List<List<V>> colorClasses;
        List<List<V>> positiveDegreeColorClasses;
        int[] maxColorDegree;
        int[] minColorDegree;
        Map<V, Integer> colorDegree;
        Map<V, Integer> coloring;
        int lastUsedColor;

        public ColoringRepresentation(Graph<V, E> graph, VertexColoringAlgorithm.Coloring<V> alpha) {
            int n = graph.vertexSet().size();
            this.colorClasses = new ArrayList(n);
            this.positiveDegreeColorClasses = new ArrayList(n);
            this.maxColorDegree = new int[n];
            this.minColorDegree = new int[n];
            this.colorDegree = new HashMap();
            this.coloring = new HashMap();
            for (int c = 0; c < n; ++c) {
                this.colorClasses.add(new ArrayList());
                this.positiveDegreeColorClasses.add(new ArrayList());
            }
            for (Object v : graph.vertexSet()) {
                this.colorClasses.get(alpha.getColors().get(v)).add(v);
                this.colorDegree.put((Integer)v, 0);
                this.coloring.put((Integer)v, alpha.getColors().get(v));
            }
            this.lastUsedColor = alpha.getNumberColors() - 1;
        }
    }
}

