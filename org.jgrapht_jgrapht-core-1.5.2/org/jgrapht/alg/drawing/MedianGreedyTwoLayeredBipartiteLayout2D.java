/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.drawing.TwoLayeredBipartiteLayout2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.util.Pair;

public class MedianGreedyTwoLayeredBipartiteLayout2D<V, E>
extends TwoLayeredBipartiteLayout2D<V, E> {
    public MedianGreedyTwoLayeredBipartiteLayout2D() {
    }

    public MedianGreedyTwoLayeredBipartiteLayout2D(Set<V> partition, Comparator<V> vertexComparator, boolean vertical) {
        super(partition, vertexComparator, vertical);
    }

    @Override
    protected void drawSecondPartition(Graph<V, E> graph, List<V> partition, LayoutModel2D<V> model) {
        if (partition.isEmpty()) {
            throw new IllegalArgumentException("Partition cannot be empty");
        }
        HashMap order = new HashMap();
        int i = 0;
        for (V v2 : partition) {
            ArrayList<Double> other = new ArrayList<Double>();
            for (E e : graph.edgesOf(v2)) {
                V u2 = Graphs.getOppositeVertex(graph, e, v2);
                Point2D p2d = model.get(u2);
                double coord = this.vertical ? p2d.getX() : p2d.getY();
                other.add(coord);
            }
            other.sort(null);
            if (other.isEmpty()) {
                order.put(v2, Pair.of(-1.7976931348623157E308, i));
            } else {
                double median = (Double)other.get(other.size() / 2);
                order.put(v2, Pair.of(median, i));
            }
            ++i;
        }
        Comparator newOrderComparator = (v, u) -> {
            Pair pv = (Pair)order.get(v);
            Pair pu = (Pair)order.get(u);
            int d = Double.compare((Double)pv.getFirst(), (Double)pu.getFirst());
            if (d != 0) {
                return d;
            }
            int degreeV = graph.degreeOf(v);
            int degreeU = graph.degreeOf(u);
            if (degreeV % 2 == 1 && degreeU % 2 == 0) {
                return -1;
            }
            if (degreeV % 2 == 0 && degreeU % 2 == 1) {
                return 1;
            }
            return Integer.compare((Integer)pv.getSecond(), (Integer)pu.getSecond());
        };
        partition.sort(newOrderComparator);
        super.drawSecondPartition(graph, partition, model);
    }
}

