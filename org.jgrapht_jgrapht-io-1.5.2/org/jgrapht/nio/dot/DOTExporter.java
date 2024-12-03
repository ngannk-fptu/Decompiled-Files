/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.dot;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.nio.dot.DOTUtils;

public class DOTExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    public static final String DEFAULT_GRAPH_ID = "G";
    private static final String INDENT = "  ";
    private final Map<V, String> validatedIds = new HashMap<V, String>();

    public DOTExporter() {
        this(new IntegerIdProvider());
    }

    public DOTExporter(Function<V, String> vertexIdProvider) {
        super(vertexIdProvider);
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        out.println(this.computeHeader(g));
        for (Map.Entry attr : this.graphAttributeProvider.orElse(Collections::emptyMap).get().entrySet()) {
            out.print(INDENT);
            out.print((String)attr.getKey());
            out.print('=');
            out.print(attr.getValue());
            out.println(";");
        }
        for (Map.Entry v : g.vertexSet()) {
            out.print(INDENT);
            out.print(this.getVertexID(v));
            this.getVertexAttributes(v).ifPresent(m -> this.renderAttributes(out, (Map<String, Attribute>)m));
            out.println(";");
        }
        String connector = this.computeConnector(g);
        for (Object e : g.edgeSet()) {
            String source = this.getVertexID(g.getEdgeSource(e));
            String target = this.getVertexID(g.getEdgeTarget(e));
            out.print(INDENT);
            out.print(source);
            out.print(connector);
            out.print(target);
            this.getEdgeAttributes(e).ifPresent(m -> this.renderAttributes(out, (Map<String, Attribute>)m));
            out.println(";");
        }
        out.println(this.computeFooter(g));
        out.flush();
    }

    private String computeHeader(Graph<V, E> graph) {
        StringBuilder headerBuilder = new StringBuilder();
        if (!graph.getType().isAllowingMultipleEdges()) {
            headerBuilder.append("strict").append(" ");
        }
        if (graph.getType().isDirected()) {
            headerBuilder.append("digraph");
        } else {
            headerBuilder.append("graph");
        }
        headerBuilder.append(" ").append(this.computeGraphId(graph)).append(" {");
        return headerBuilder.toString();
    }

    private String computeFooter(Graph<V, E> graph) {
        return "}";
    }

    private String computeConnector(Graph<V, E> graph) {
        StringBuilder connectorBuilder = new StringBuilder();
        if (graph.getType().isDirected()) {
            connectorBuilder.append(" ").append("->").append(" ");
        } else {
            connectorBuilder.append(" ").append("--").append(" ");
        }
        return connectorBuilder.toString();
    }

    private String computeGraphId(Graph<V, E> graph) {
        String graphId = this.getGraphId().orElse(DEFAULT_GRAPH_ID);
        if (!DOTUtils.isValidID(graphId)) {
            throw new ExportException("Generated graph ID '" + graphId + "' is not valid with respect to the .dot language");
        }
        return graphId;
    }

    private void renderAttributes(PrintWriter out, Map<String, Attribute> attributes) {
        if (attributes == null) {
            return;
        }
        out.print(" [ ");
        for (Map.Entry<String, Attribute> entry : attributes.entrySet()) {
            String name = entry.getKey();
            this.renderAttribute(out, name, entry.getValue());
        }
        out.print("]");
    }

    private void renderAttribute(PrintWriter out, String attrName, Attribute attribute) {
        out.print(attrName + "=");
        String attrValue = attribute.getValue();
        if (AttributeType.HTML.equals((Object)attribute.getType())) {
            out.print("<" + attrValue + ">");
        } else if (AttributeType.IDENTIFIER.equals((Object)attribute.getType())) {
            out.print(attrValue);
        } else {
            out.print("\"" + DOTExporter.escapeDoubleQuotes(attrValue) + "\"");
        }
        out.print(" ");
    }

    private static String escapeDoubleQuotes(String labelName) {
        return labelName.replaceAll("\"", Matcher.quoteReplacement("\\\""));
    }

    private String getVertexID(V v) {
        String vertexId = this.validatedIds.get(v);
        if (vertexId == null) {
            vertexId = this.getVertexId(v);
            if (!DOTUtils.isValidID(vertexId)) {
                throw new ExportException("Generated id '" + vertexId + "'for vertex '" + v + "' is not valid with respect to the .dot language");
            }
            this.validatedIds.put((String)v, vertexId);
        }
        return vertexId;
    }
}

