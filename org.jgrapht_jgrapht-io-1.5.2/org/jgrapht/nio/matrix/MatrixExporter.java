/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.Graphs
 *  org.jgrapht.util.ModifiableInteger
 */
package org.jgrapht.nio.matrix;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.util.ModifiableInteger;

public class MatrixExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    private final String delimiter = " ";
    private Format format;

    public MatrixExporter() {
        this(Format.SPARSE_ADJACENCY_MATRIX, new IntegerIdProvider());
    }

    public MatrixExporter(Format format) {
        this(format, new IntegerIdProvider());
    }

    public MatrixExporter(Format format, Function<V, String> vertexIdProvider) {
        super(vertexIdProvider);
        this.format = format;
    }

    public Format getFormat() {
        return this.format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) throws ExportException {
        switch (this.format) {
            case SPARSE_ADJACENCY_MATRIX: {
                this.exportAdjacencyMatrix(g, writer);
                break;
            }
            case SPARSE_LAPLACIAN_MATRIX: {
                if (g.getType().isUndirected()) {
                    this.exportLaplacianMatrix(g, writer);
                    break;
                }
                throw new ExportException("Exporter can only export undirected graphs in this format");
            }
            case SPARSE_NORMALIZED_LAPLACIAN_MATRIX: {
                if (g.getType().isUndirected()) {
                    this.exportNormalizedLaplacianMatrix(g, writer);
                    break;
                }
                throw new ExportException("Exporter can only export undirected graphs in this format");
            }
        }
    }

    private void exportAdjacencyMatrix(Graph<V, E> g, Writer writer) {
        for (Object from : g.vertexSet()) {
            this.getVertexId(from);
        }
        PrintWriter out = new PrintWriter(writer);
        if (g.getType().isDirected()) {
            for (Object from : g.vertexSet()) {
                this.exportAdjacencyMatrixVertex(out, from, Graphs.successorListOf(g, from));
            }
        } else {
            for (Object from : g.vertexSet()) {
                this.exportAdjacencyMatrixVertex(out, from, Graphs.neighborListOf(g, from));
            }
        }
        out.flush();
    }

    private void exportAdjacencyMatrixVertex(PrintWriter writer, V from, List<V> neighbors) {
        ModifiableInteger count;
        String toName;
        String fromName = this.getVertexId(from);
        LinkedHashMap<String, ModifiableInteger> counts = new LinkedHashMap<String, ModifiableInteger>();
        for (V v : neighbors) {
            toName = this.getVertexId(v);
            count = (ModifiableInteger)counts.get(toName);
            if (count == null) {
                count = new ModifiableInteger(0);
                counts.put(toName, count);
            }
            count.increment();
            if (!from.equals(v)) continue;
            count.increment();
        }
        for (Map.Entry entry : counts.entrySet()) {
            toName = (String)entry.getKey();
            count = (ModifiableInteger)entry.getValue();
            this.exportEntry(writer, fromName, toName, count.toString());
        }
    }

    private void exportEntry(PrintWriter writer, String from, String to, String value) {
        writer.println(from + " " + to + " " + value);
    }

    private void exportLaplacianMatrix(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        for (Object from : g.vertexSet()) {
            this.getVertexId(from);
        }
        for (Object from : g.vertexSet()) {
            String fromName = this.getVertexId(from);
            List neighbors = Graphs.neighborListOf(g, from);
            this.exportEntry(out, fromName, fromName, Integer.toString(neighbors.size()));
            for (Object to : neighbors) {
                String toName = this.getVertexId(to);
                this.exportEntry(out, fromName, toName, "-1");
            }
        }
        out.flush();
    }

    private void exportNormalizedLaplacianMatrix(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        for (Object from : g.vertexSet()) {
            this.getVertexId(from);
        }
        for (Object from : g.vertexSet()) {
            String fromName = this.getVertexId(from);
            LinkedHashSet neighbors = new LinkedHashSet(Graphs.neighborListOf(g, from));
            if (neighbors.isEmpty()) {
                this.exportEntry(out, fromName, fromName, "0");
                continue;
            }
            this.exportEntry(out, fromName, fromName, "1");
            for (Object to : neighbors) {
                String toName = this.getVertexId(to);
                double value = -1.0 / Math.sqrt(g.degreeOf(from) * g.degreeOf(to));
                this.exportEntry(out, fromName, toName, Double.toString(value));
            }
        }
        out.flush();
    }

    public static enum Format {
        SPARSE_ADJACENCY_MATRIX,
        SPARSE_LAPLACIAN_MATRIX,
        SPARSE_NORMALIZED_LAPLACIAN_MATRIX;

    }
}

