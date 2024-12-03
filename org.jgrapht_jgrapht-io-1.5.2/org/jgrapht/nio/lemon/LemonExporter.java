/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.lemon;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.text.StringEscapeUtils;
import org.jgrapht.Graph;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;

public class LemonExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    private static final String CREATOR = "JGraphT Lemon (LGF) Exporter";
    private static final String VERSION = "1";
    private static final String DELIM = " ";
    private static final String TAB1 = "\t";
    private final Set<Parameter> parameters = new HashSet<Parameter>();

    public LemonExporter() {
        this(new IntegerIdProvider());
    }

    public LemonExporter(Function<V, String> vertexIdProvider) {
        super(vertexIdProvider);
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        this.exportHeader(out);
        this.exportVertices(out, g);
        this.exportEdges(out, g);
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

    private String prepareId(String s) {
        boolean escapeStringAsJava = this.parameters.contains((Object)Parameter.ESCAPE_STRINGS_AS_JAVA);
        if (escapeStringAsJava) {
            return "\"" + StringEscapeUtils.escapeJava((String)s) + "\"";
        }
        return s;
    }

    private void exportHeader(PrintWriter out) {
        out.println("#Creator: JGraphT Lemon (LGF) Exporter");
        out.println("#Version: 1");
        out.println();
    }

    private void exportVertices(PrintWriter out, Graph<V, E> g) {
        out.println("@nodes");
        out.println("label");
        for (Object v : g.vertexSet()) {
            String id = this.getVertexId(v);
            String quotedId = this.prepareId(id);
            out.println(quotedId);
        }
        out.println();
    }

    private void exportEdges(PrintWriter out, Graph<V, E> g) {
        boolean exportEdgeWeights = this.parameters.contains((Object)Parameter.EXPORT_EDGE_WEIGHTS);
        out.println("@arcs");
        out.print(TAB1);
        out.print(TAB1);
        if (exportEdgeWeights) {
            out.println("weight");
        } else {
            out.println("-");
        }
        for (Object edge : g.edgeSet()) {
            String s = this.getVertexId(g.getEdgeSource(edge));
            String t = this.getVertexId(g.getEdgeTarget(edge));
            out.print(this.prepareId(s));
            out.print(TAB1);
            out.print(this.prepareId(t));
            if (exportEdgeWeights) {
                out.print(TAB1);
                out.print(Double.toString(g.getEdgeWeight(edge)));
            }
            out.println();
        }
        out.println();
    }

    public static enum Parameter {
        EXPORT_EDGE_WEIGHTS,
        ESCAPE_STRINGS_AS_JAVA;

    }
}

