/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.Graphs
 */
package org.jgrapht.nio.csv;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.nio.csv.CSVFormat;
import org.jgrapht.nio.csv.DSVUtils;

public class CSVExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    private static final char DEFAULT_DELIMITER = ',';
    private final Set<CSVFormat.Parameter> parameters;
    private CSVFormat format;
    private char delimiter;

    public CSVExporter() {
        this(CSVFormat.ADJACENCY_LIST);
    }

    public CSVExporter(CSVFormat format) {
        this(format, ',');
    }

    public CSVExporter(CSVFormat format, char delimiter) {
        this(new IntegerIdProvider(), format, delimiter);
    }

    public CSVExporter(Function<V, String> vertexIdProvider, CSVFormat format, char delimiter) {
        super(vertexIdProvider);
        this.format = format;
        if (!DSVUtils.isValidDelimiter(delimiter)) {
            throw new IllegalArgumentException("Character cannot be used as a delimiter");
        }
        this.delimiter = delimiter;
        this.parameters = new HashSet<CSVFormat.Parameter>();
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        switch (this.format) {
            case EDGE_LIST: {
                this.exportAsEdgeList(g, out);
                break;
            }
            case ADJACENCY_LIST: {
                this.exportAsAdjacencyList(g, out);
                break;
            }
            case MATRIX: {
                this.exportAsMatrix(g, out);
            }
        }
        out.flush();
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

    private void exportAsEdgeList(Graph<V, E> g, PrintWriter out) {
        boolean exportEdgeWeights = this.parameters.contains((Object)CSVFormat.Parameter.EDGE_WEIGHTS);
        for (Object e : g.edgeSet()) {
            this.exportEscapedField(out, this.getVertexId(g.getEdgeSource(e)));
            out.print(this.delimiter);
            this.exportEscapedField(out, this.getVertexId(g.getEdgeTarget(e)));
            if (exportEdgeWeights) {
                out.print(this.delimiter);
                this.exportEscapedField(out, String.valueOf(g.getEdgeWeight(e)));
            }
            out.println();
        }
    }

    private void exportAsAdjacencyList(Graph<V, E> g, PrintWriter out) {
        boolean exportEdgeWeights = this.parameters.contains((Object)CSVFormat.Parameter.EDGE_WEIGHTS);
        for (Object v : g.vertexSet()) {
            this.exportEscapedField(out, this.getVertexId(v));
            for (Object e : g.outgoingEdgesOf(v)) {
                Object w = Graphs.getOppositeVertex(g, e, v);
                out.print(this.delimiter);
                this.exportEscapedField(out, this.getVertexId(w));
                if (!exportEdgeWeights) continue;
                out.print(this.delimiter);
                this.exportEscapedField(out, String.valueOf(g.getEdgeWeight(e)));
            }
            out.println();
        }
    }

    private void exportAsMatrix(Graph<V, E> g, PrintWriter out) {
        boolean exportNodeId = this.parameters.contains((Object)CSVFormat.Parameter.MATRIX_FORMAT_NODEID);
        boolean exportEdgeWeights = this.parameters.contains((Object)CSVFormat.Parameter.EDGE_WEIGHTS);
        boolean zeroWhenNoEdge = this.parameters.contains((Object)CSVFormat.Parameter.MATRIX_FORMAT_ZERO_WHEN_NO_EDGE);
        if (exportNodeId) {
            for (Object v : g.vertexSet()) {
                out.print(this.delimiter);
                this.exportEscapedField(out, this.getVertexId(v));
            }
            out.println();
        }
        int n = g.vertexSet().size();
        for (Object v : g.vertexSet()) {
            if (exportNodeId) {
                this.exportEscapedField(out, this.getVertexId(v));
                out.print(this.delimiter);
            }
            int i = 0;
            for (Object u : g.vertexSet()) {
                Object e = g.getEdge(v, u);
                if (e == null) {
                    if (zeroWhenNoEdge) {
                        this.exportEscapedField(out, "0");
                    }
                } else if (exportEdgeWeights) {
                    this.exportEscapedField(out, String.valueOf(g.getEdgeWeight(e)));
                } else {
                    this.exportEscapedField(out, "1");
                }
                if (i++ >= n - 1) continue;
                out.print(this.delimiter);
            }
            out.println();
        }
    }

    private void exportEscapedField(PrintWriter out, String field) {
        out.print(DSVUtils.escapeDSV(field, this.delimiter));
    }
}

