/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.dimacs;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.nio.dimacs.DIMACSFormat;

public class DIMACSExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    public static final DIMACSFormat DEFAULT_DIMACS_FORMAT = DIMACSFormat.MAX_CLIQUE;
    private static final String HEADER = "Generated using the JGraphT library";
    private final Set<Parameter> parameters;
    private DIMACSFormat format;

    public DIMACSExporter() {
        this(new IntegerIdProvider());
    }

    public DIMACSExporter(Function<V, String> vertexIdProvider) {
        this(vertexIdProvider, DEFAULT_DIMACS_FORMAT);
    }

    public DIMACSExporter(Function<V, String> vertexIdProvider, DIMACSFormat format) {
        super(vertexIdProvider);
        this.format = Objects.requireNonNull(format, "Format cannot be null");
        this.parameters = new HashSet<Parameter>();
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        out.println("c");
        out.println("c SOURCE: Generated using the JGraphT library");
        out.println("c");
        out.println("p " + this.format.getProblem() + " " + g.vertexSet().size() + " " + g.edgeSet().size());
        boolean exportEdgeWeights = this.parameters.contains((Object)Parameter.EXPORT_EDGE_WEIGHTS);
        for (Object edge : g.edgeSet()) {
            out.print(this.format.getEdgeDescriptor());
            out.print(" ");
            out.print(this.getVertexId(g.getEdgeSource(edge)));
            out.print(" ");
            out.print(this.getVertexId(g.getEdgeTarget(edge)));
            if (exportEdgeWeights) {
                out.print(" ");
                out.print(Double.toString(g.getEdgeWeight(edge)));
            }
            out.println();
        }
        out.flush();
    }

    public boolean isParameter(Parameter p) {
        return this.parameters.contains((Object)p);
    }

    public void setParameter(Parameter p, boolean value) {
        if (value) {
            this.parameters.add(p);
        } else {
            this.parameters.remove((Object)p);
        }
    }

    public DIMACSFormat getFormat() {
        return this.format;
    }

    public void setFormat(DIMACSFormat format) {
        this.format = Objects.requireNonNull(format, "Format cannot be null");
    }

    public static enum Parameter {
        EXPORT_EDGE_WEIGHTS;

    }
}

