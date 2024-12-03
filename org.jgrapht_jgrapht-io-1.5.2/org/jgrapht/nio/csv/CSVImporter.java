/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.GraphType
 *  org.jgrapht.alg.util.Triple
 */
package org.jgrapht.nio.csv;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.nio.BaseEventDrivenImporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphImporter;
import org.jgrapht.nio.ImportException;
import org.jgrapht.nio.csv.CSVEventDrivenImporter;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.csv.DSVUtils;

public class CSVImporter<V, E>
extends BaseEventDrivenImporter<V, E>
implements GraphImporter<V, E> {
    private static final char DEFAULT_DELIMITER = ',';
    private static final String DEFAULT_VERTEX_ID_KEY = "ID";
    private static final String DEFAULT_WEIGHT_KEY = "weight";
    private CSVFormat format;
    private char delimiter;
    private final Set<CSVFormat.Parameter> parameters;
    private Function<String, V> vertexFactory;

    public CSVImporter() {
        this(CSVFormat.ADJACENCY_LIST, ',');
    }

    public CSVImporter(CSVFormat format) {
        this(format, ',');
    }

    public CSVImporter(CSVFormat format, char delimiter) {
        this.format = format;
        if (!DSVUtils.isValidDelimiter(delimiter)) {
            throw new IllegalArgumentException("Character cannot be used as a delimiter");
        }
        this.delimiter = delimiter;
        this.parameters = new HashSet<CSVFormat.Parameter>();
    }

    public CSVFormat getFormat() {
        return this.format;
    }

    public void setFormat(CSVFormat format) {
        this.format = format;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public void setDelimiter(char delimiter) {
        if (!DSVUtils.isValidDelimiter(delimiter)) {
            throw new IllegalArgumentException("Character cannot be used as a delimiter");
        }
        this.delimiter = delimiter;
    }

    public boolean isParameter(CSVFormat.Parameter p) {
        return this.parameters.contains((Object)p);
    }

    public void setParameter(CSVFormat.Parameter p, boolean value) {
        if (value) {
            this.parameters.add(p);
        } else {
            this.parameters.remove((Object)p);
        }
    }

    public Function<String, V> getVertexFactory() {
        return this.vertexFactory;
    }

    public void setVertexFactory(Function<String, V> vertexFactory) {
        this.vertexFactory = vertexFactory;
    }

    @Override
    public void importGraph(Graph<V, E> graph, Reader input) throws ImportException {
        CSVEventDrivenImporter genericImporter = new CSVEventDrivenImporter();
        genericImporter.setDelimiter(this.delimiter);
        genericImporter.setFormat(this.format);
        genericImporter.setParameter(CSVFormat.Parameter.EDGE_WEIGHTS, this.isParameter(CSVFormat.Parameter.EDGE_WEIGHTS));
        genericImporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID, this.isParameter(CSVFormat.Parameter.MATRIX_FORMAT_NODEID));
        genericImporter.setParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE, this.isParameter(CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE));
        Consumers consumers = new Consumers(graph);
        genericImporter.addVertexConsumer(consumers.vertexConsumer);
        genericImporter.addEdgeConsumer(consumers.edgeConsumer);
        genericImporter.importInput(input);
    }

    private class Consumers {
        private Graph<V, E> graph;
        private GraphType graphType;
        private Map<String, V> map;
        public final Consumer<String> vertexConsumer = t -> {
            Object v;
            if (this.map.containsKey(t)) {
                throw new ImportException("Node " + t + " already exists");
            }
            if (CSVImporter.this.vertexFactory != null) {
                v = CSVImporter.this.vertexFactory.apply((String)t);
                this.graph.addVertex(v);
            } else {
                v = this.graph.addVertex();
            }
            this.map.put((String)t, v);
            CSVImporter.this.notifyVertex(v);
            CSVImporter.this.notifyVertexAttribute(v, CSVImporter.DEFAULT_VERTEX_ID_KEY, DefaultAttribute.createAttribute(t));
        };
        public final Consumer<Triple<String, String, Double>> edgeConsumer = t -> {
            String source = (String)t.getFirst();
            Object from = this.map.get(t.getFirst());
            if (from == null) {
                throw new ImportException("Node " + source + " does not exist");
            }
            String target = (String)t.getSecond();
            Object to = this.map.get(target);
            if (to == null) {
                throw new ImportException("Node " + target + " does not exist");
            }
            Object e = this.graph.addEdge(from, to);
            if (this.graphType.isWeighted() && t.getThird() != null) {
                this.graph.setEdgeWeight(e, ((Double)t.getThird()).doubleValue());
            }
            CSVImporter.this.notifyEdge(e);
            if (this.graphType.isWeighted() && t.getThird() != null) {
                CSVImporter.this.notifyEdgeAttribute(e, CSVImporter.DEFAULT_WEIGHT_KEY, DefaultAttribute.createAttribute((Double)t.getThird()));
            }
        };

        public Consumers(Graph<V, E> graph) {
            this.graph = graph;
            this.graphType = graph.getType();
            this.map = new HashMap();
        }
    }
}

