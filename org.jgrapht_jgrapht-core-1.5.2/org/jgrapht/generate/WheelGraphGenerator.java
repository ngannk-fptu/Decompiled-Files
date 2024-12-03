/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.RingGraphGenerator;
import org.jgrapht.graph.GraphDelegator;

public class WheelGraphGenerator<V, E>
implements GraphGenerator<V, E, V> {
    public static final String HUB_VERTEX = "Hub Vertex";
    private boolean inwardSpokes;
    private int size;

    public WheelGraphGenerator(int size) {
        this(size, true);
    }

    public WheelGraphGenerator(int size, boolean inwardSpokes) {
        if (size < 0) {
            throw new IllegalArgumentException("must be non-negative");
        }
        this.size = size;
        this.inwardSpokes = inwardSpokes;
    }

    @Override
    public void generateGraph(Graph<V, E> target, Map<String, V> resultMap) {
        if (this.size < 1) {
            return;
        }
        ArrayList rim = new ArrayList();
        Supplier initialSupplier = target.getVertexSupplier();
        Supplier<Object> rimVertexSupplier = () -> {
            Object vertex = initialSupplier.get();
            rim.add(vertex);
            return vertex;
        };
        GraphDelegator<Object, E> targetWithRimVertexSupplier = new GraphDelegator<Object, E>(target, rimVertexSupplier, null);
        new RingGraphGenerator<Object, E>(this.size - 1).generateGraph((Graph<Object, E>)targetWithRimVertexSupplier, (Map<String, Object>)resultMap);
        V hubVertex = target.addVertex();
        if (resultMap != null) {
            resultMap.put(HUB_VERTEX, hubVertex);
        }
        for (Object rimVertex : rim) {
            if (this.inwardSpokes) {
                target.addEdge(rimVertex, hubVertex);
                continue;
            }
            target.addEdge(hubVertex, rimVertex);
        }
    }
}

