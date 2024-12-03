/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.dimacs;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.dimacs.DIMACSEventDrivenImporter;

public class DIMACSImporter<V, E>
extends BaseEventDrivenImporter<V, E>
implements GraphImporter<V, E> {
    public static final String DEFAULT_VERTEX_ID_KEY = "ID";
    private Function<Integer, V> vertexFactory;
    private final double defaultWeight;

    public DIMACSImporter(double defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public DIMACSImporter() {
        this(1.0);
    }

    public Function<Integer, V> getVertexFactory() {
        return this.vertexFactory;
    }

    public void setVertexFactory(Function<Integer, V> vertexFactory) {
        this.vertexFactory = vertexFactory;
    }

    @Override
    public void importGraph(Graph<V, E> graph, Reader input) throws ImportException {
        DIMACSEventDrivenImporter genericImporter = new DIMACSEventDrivenImporter().renumberVertices(false).zeroBasedNumbering(false);
        Consumers consumers = new Consumers(graph);
        genericImporter.addVertexCountConsumer(consumers.nodeCountConsumer);
        genericImporter.addEdgeConsumer(consumers.edgeConsumer);
        genericImporter.importInput(input);
    }

    private static <E> E getElement(List<E> list, int index) {
        return index < list.size() ? (E)list.get(index) : null;
    }

    private class Consumers {
        private Graph<V, E> graph;
        private List<V> list;
        public final Consumer<Integer> nodeCountConsumer = n -> {
            for (int i = 1; i <= n; ++i) {
                Object v;
                if (DIMACSImporter.this.vertexFactory != null) {
                    v = DIMACSImporter.this.vertexFactory.apply(i);
                    this.graph.addVertex(v);
                } else {
                    v = this.graph.addVertex();
                }
                this.list.add(v);
                DIMACSImporter.this.notifyVertex(v);
                DIMACSImporter.this.notifyVertexAttribute(v, DIMACSImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(i));
            }
        };
        public final Consumer<Triple<Integer, Integer, Double>> edgeConsumer = t -> {
            int source = (Integer)t.getFirst();
            Object from = DIMACSImporter.getElement(this.list, source - 1);
            if (from == null) {
                throw new ImportException("Node " + source + " does not exist");
            }
            int target = (Integer)t.getSecond();
            Object to = DIMACSImporter.getElement(this.list, target - 1);
            if (to == null) {
                throw new ImportException("Node " + target + " does not exist");
            }
            Object e = this.graph.addEdge(from, to);
            if (this.graph.getType().isWeighted()) {
                double weight = t.getThird() == null ? DIMACSImporter.this.defaultWeight : (Double)t.getThird();
                this.graph.setEdgeWeight(e, weight);
            }
            DIMACSImporter.this.notifyEdge(e);
        };

        public Consumers(Graph<V, E> graph) {
            this.graph = graph;
            this.list = new ArrayList();
        }
    }
}

