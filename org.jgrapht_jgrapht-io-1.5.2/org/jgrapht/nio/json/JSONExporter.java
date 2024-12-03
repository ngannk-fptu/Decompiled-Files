/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.json;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.text.StringEscapeUtils;
import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;

public class JSONExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    public static final String DEFAULT_VERTICES_COLLECTION_NAME = "nodes";
    public static final String DEFAULT_EDGES_COLLECTION_NAME = "edges";
    private static final String CREATOR = "JGraphT JSON Exporter";
    private static final String VERSION = "1";
    private String verticesCollectionName = "nodes";
    private String edgesCollectionName = "edges";

    public JSONExporter() {
        this(new IntegerIdProvider());
    }

    public JSONExporter(Function<V, String> vertexIdProvider) {
        super(vertexIdProvider);
    }

    public String getVerticesCollectionName() {
        return this.verticesCollectionName;
    }

    public void setVerticesCollectionName(String verticesCollectionName) {
        this.verticesCollectionName = Objects.requireNonNull(verticesCollectionName);
    }

    public String getEdgesCollectionName() {
        return this.edgesCollectionName;
    }

    public void setEdgesCollectionName(String edgesCollectionName) {
        this.edgesCollectionName = Objects.requireNonNull(edgesCollectionName);
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        out.print('{');
        out.print(this.quoted("creator"));
        out.print(':');
        out.print(this.quoted(CREATOR));
        out.print(',');
        out.print(this.quoted("version"));
        out.print(':');
        out.print(this.quoted(VERSION));
        out.print(',');
        out.print(this.quoted(this.verticesCollectionName));
        out.print(':');
        out.print('[');
        boolean printComma = false;
        for (Object v : g.vertexSet()) {
            if (!printComma) {
                printComma = true;
            } else {
                out.print(',');
            }
            this.exportVertex(out, g, v);
        }
        out.print("]");
        out.print(',');
        out.print(this.quoted(this.edgesCollectionName));
        out.print(':');
        out.print('[');
        printComma = false;
        for (Object e : g.edgeSet()) {
            if (!printComma) {
                printComma = true;
            } else {
                out.print(',');
            }
            this.exportEdge(out, g, e);
        }
        out.print("]");
        out.print('}');
        out.flush();
    }

    private void exportVertex(PrintWriter out, Graph<V, E> g, V v) {
        String vertexId = (String)this.vertexIdProvider.apply(v);
        out.print('{');
        out.print(this.quoted("id"));
        out.print(':');
        out.print(this.quoted(vertexId));
        this.exportVertexAttributes(out, g, v);
        out.print('}');
    }

    private void exportEdge(PrintWriter out, Graph<V, E> g, E e) {
        Object source = g.getEdgeSource(e);
        String sourceId = (String)this.vertexIdProvider.apply(source);
        Object target = g.getEdgeTarget(e);
        String targetId = (String)this.vertexIdProvider.apply(target);
        out.print('{');
        this.edgeIdProvider.ifPresent(p -> {
            String edgeId = (String)p.apply(e);
            if (edgeId != null) {
                out.print(this.quoted("id"));
                out.print(':');
                out.print(this.quoted(edgeId));
                out.print(',');
            }
        });
        out.print(this.quoted("source"));
        out.print(':');
        out.print(this.quoted(sourceId));
        out.print(',');
        out.print(this.quoted("target"));
        out.print(':');
        out.print(this.quoted(targetId));
        this.exportEdgeAttributes(out, g, e);
        out.print('}');
    }

    private void exportVertexAttributes(PrintWriter out, Graph<V, E> g, V v) {
        if (!this.vertexAttributeProvider.isPresent()) {
            return;
        }
        ((Map)((Function)this.vertexAttributeProvider.get()).apply(v)).entrySet().stream().filter(e -> !((String)e.getKey()).equals("id")).forEach(entry -> {
            out.print(",");
            out.print(this.quoted((String)entry.getKey()));
            out.print(":");
            this.outputValue(out, (Attribute)entry.getValue());
        });
    }

    private void exportEdgeAttributes(PrintWriter out, Graph<V, E> g, E e) {
        if (!this.edgeAttributeProvider.isPresent()) {
            return;
        }
        Set<String> forbidden = Set.of("id", "source", "target");
        ((Map)((Function)this.edgeAttributeProvider.get()).apply(e)).entrySet().stream().filter(entry -> !forbidden.contains(entry.getKey())).forEach(entry -> {
            out.print(",");
            out.print(this.quoted((String)entry.getKey()));
            out.print(":");
            this.outputValue(out, (Attribute)entry.getValue());
        });
    }

    private void outputValue(PrintWriter out, Attribute value) {
        AttributeType type = value.getType();
        if (type.equals((Object)AttributeType.BOOLEAN)) {
            boolean booleanValue = Boolean.parseBoolean(value.getValue());
            out.print(booleanValue ? "true" : "false");
        } else if (type.equals((Object)AttributeType.INT)) {
            out.print(Integer.parseInt(value.getValue()));
        } else if (type.equals((Object)AttributeType.LONG)) {
            out.print(Long.parseLong(value.getValue()));
        } else if (type.equals((Object)AttributeType.FLOAT)) {
            float floatValue = Float.parseFloat(value.getValue());
            if (!Float.isFinite(floatValue)) {
                throw new IllegalArgumentException("Infinity and NaN not allowed in JSON");
            }
            out.print(floatValue);
        } else if (type.equals((Object)AttributeType.DOUBLE)) {
            double doubleValue = Double.parseDouble(value.getValue());
            if (!Double.isFinite(doubleValue)) {
                throw new IllegalArgumentException("Infinity and NaN not allowed in JSON");
            }
            out.print(doubleValue);
        } else {
            out.print(this.quoted(value.toString()));
        }
    }

    private String quoted(String s) {
        return "\"" + StringEscapeUtils.escapeJson((String)s) + "\"";
    }
}

