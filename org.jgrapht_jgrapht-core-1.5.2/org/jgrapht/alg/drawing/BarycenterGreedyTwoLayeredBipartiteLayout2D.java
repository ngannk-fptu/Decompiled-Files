/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.alg.drawing;

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

public class BarycenterGreedyTwoLayeredBipartiteLayout2D<V, E>
extends TwoLayeredBipartiteLayout2D<V, E> {
    public BarycenterGreedyTwoLayeredBipartiteLayout2D() {
    }

    public BarycenterGreedyTwoLayeredBipartiteLayout2D(Set<V> partition, Comparator<V> vertexComparator, boolean vertical) {
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
            int degree = graph.degreeOf(v2);
            if (degree == 0) {
                order.put(v2, Pair.of(-1.7976931348623157E308, i));
            } else {
                double barycenter = 0.0;
                for (E e : graph.outgoingEdgesOf(v2)) {
                    V u2 = Graphs.getOppositeVertex(graph, e, v2);
                    Point2D p2d = model.get(u2);
                    double coord = this.vertical ? p2d.getX() : p2d.getY();
                    barycenter += coord;
                }
                order.put(v2, Pair.of(barycenter /= (double)degree, i));
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

