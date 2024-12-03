/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.LayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.interfaces.PartitioningAlgorithm;
import org.jgrapht.alg.partition.BipartitePartitioning;
import org.jgrapht.alg.util.Pair;

public class TwoLayeredBipartiteLayout2D<V, E>
implements LayoutAlgorithm2D<V, E> {
    protected Comparator<V> vertexComparator;
    protected boolean vertical;
    protected Set<V> partition;

    public TwoLayeredBipartiteLayout2D() {
        this(null, null, true);
    }

    public TwoLayeredBipartiteLayout2D(Set<V> partition, Comparator<V> vertexComparator, boolean vertical) {
        this.partition = partition;
        this.vertexComparator = vertexComparator;
        this.vertical = vertical;
    }

    public TwoLayeredBipartiteLayout2D<V, E> withVertexComparator(Comparator<V> vertexComparator) {
        this.vertexComparator = vertexComparator;
        return this;
    }

    public TwoLayeredBipartiteLayout2D<V, E> withVertical(boolean vertical) {
        this.vertical = vertical;
        return this;
    }

    public TwoLayeredBipartiteLayout2D<V, E> withFirstPartition(Set<V> partition) {
        this.partition = partition;
        return this;
    }

    @Override
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {
        Pair<List<V>, List<V>> partitions = this.computePartitions(graph);
        this.drawFirstPartition(graph, partitions.getFirst(), model);
        this.drawSecondPartition(graph, partitions.getSecond(), model);
    }

    protected void drawFirstPartition(Graph<V, E> graph, List<V> partition, LayoutModel2D<V> model) {
        if (partition.isEmpty()) {
            throw new IllegalArgumentException("Partition cannot be empty");
        }
        Box2D drawableArea = model.getDrawableArea();
        double height = drawableArea.getHeight();
        double width = drawableArea.getWidth();
        double minX = drawableArea.getMinX();
        double minY = drawableArea.getMinY();
        int n = partition.size();
        double step = 0.0;
        if (n > 1) {
            step = (this.vertical ? height : width) / (double)(n - 1);
        }
        if (this.vertical) {
            double y = minY;
            for (V v : partition) {
                model.put(v, Point2D.of(minX, y));
                y += step;
            }
        } else {
            double x = minX;
            for (V v : partition) {
                model.put(v, Point2D.of(x, minY));
                x += step;
            }
        }
    }

    protected void drawSecondPartition(Graph<V, E> graph, List<V> partition, LayoutModel2D<V> model) {
        if (partition.isEmpty()) {
            throw new IllegalArgumentException("Partition cannot be empty");
        }
        Box2D drawableArea = model.getDrawableArea();
        double height = drawableArea.getHeight();
        double width = drawableArea.getWidth();
        double minX = drawableArea.getMinX();
        double minY = drawableArea.getMinY();
        int n = partition.size();
        double step = 0.0;
        if (n > 1) {
            step = (this.vertical ? height : width) / (double)(n - 1);
        }
        if (this.vertical) {
            double y = minY;
            for (V v : partition) {
                model.put(v, Point2D.of(minX + width, y));
                y += step;
            }
        } else {
            double x = minX;
            for (V v : partition) {
                model.put(v, Point2D.of(x, minY + height));
                x += step;
            }
        }
    }

    protected Pair<List<V>, List<V>> computePartitions(Graph<V, E> graph) {
        ArrayList<V> left = new ArrayList<V>();
        ArrayList<V> right = new ArrayList<V>();
        if (this.partition != null) {
            for (V v : graph.vertexSet()) {
                if (this.partition.contains(v)) {
                    left.add(v);
                    continue;
                }
                right.add(v);
            }
            for (Object e : graph.edgeSet()) {
                V v = graph.getEdgeSource(e);
                V u = graph.getEdgeTarget(e);
                if (this.partition.contains(v) ^ this.partition.contains(u)) continue;
                throw new IllegalArgumentException("Invalid provided bipartite partition.");
            }
        } else {
            PartitioningAlgorithm.Partitioning<V> partitioning = new BipartitePartitioning<V, E>(graph).getPartitioning();
            if (partitioning == null) {
                throw new IllegalArgumentException("Graph is not bipartite.");
            }
            left.addAll(partitioning.getPartition(0));
            right.addAll(partitioning.getPartition(1));
        }
        if (this.vertexComparator != null) {
            left.sort(this.vertexComparator);
            right.sort(this.vertexComparator);
        }
        return Pair.of(left, right);
    }
}

