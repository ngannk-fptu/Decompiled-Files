/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.text.StringEscapeUtils
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.gml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.apache.commons.text.StringEscapeUtils;
import org.jgrapht.Graph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;

public class GmlExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    private static final String CREATOR = "JGraphT GML Exporter";
    private static final String VERSION = "1";
    private static final String DELIM = " ";
    private static final String TAB1 = "\t";
    private static final String TAB2 = "\t\t";
    private static final String LABEL_ATTRIBUTE_KEY = "label";
    private static final String WEIGHT_ATTRIBUTE_KEY = "weight";
    private static final Set<String> FORBIDDEN_VERTEX_CUSTOM_ATTRIBUTE_KEYS = Set.of("id");
    private static final Set<String> FORBIDDEN_EDGE_CUSTOM_ATTRIBUTE_KEYS = Set.of("id", "source", "target");
    private final Set<Parameter> parameters = new HashSet<Parameter>();

    public GmlExporter() {
        this(new IntegerIdProvider());
    }

    public GmlExporter(Function<V, String> vertexIdProvider) {
        super(vertexIdProvider);
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        for (Object from : g.vertexSet()) {
            this.getVertexId(from);
        }
        this.exportHeader(out);
        out.println("graph");
        out.println("[");
        out.println("\tlabel " + this.quoted(""));
        if (g.getType().isDirected()) {
            out.println("\tdirected 1");
        } else {
            out.println("\tdirected 0");
        }
        this.exportVertices(out, g);
        this.exportEdges(out, g);
        out.println("]");
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

    private String quoted(String s) {
        boolean escapeStringAsJava = this.parameters.contains((Object)Parameter.ESCAPE_STRINGS_AS_JAVA);
        if (escapeStringAsJava) {
            return "\"" + StringEscapeUtils.escapeJava((String)s) + "\"";
        }
        return "\"" + s + "\"";
    }

    private void exportHeader(PrintWriter out) {
        out.println("Creator " + this.quoted(CREATOR));
        out.println("Version 1");
    }

    private void exportAttribute(PrintWriter out, String key, Attribute attribute) {
        AttributeType type = attribute.getType();
        switch (type) {
            case INT: {
                out.println(TAB2 + key + DELIM + Integer.valueOf(attribute.getValue()));
                break;
            }
            case LONG: {
                out.println(TAB2 + key + DELIM + Long.valueOf(attribute.getValue()));
                break;
            }
            case FLOAT: {
                out.println(TAB2 + key + DELIM + Float.valueOf(attribute.getValue()));
                break;
            }
            case DOUBLE: {
                out.println(TAB2 + key + DELIM + Double.valueOf(attribute.getValue()));
                break;
            }
            default: {
                out.println(TAB2 + key + DELIM + this.quoted(attribute.getValue()));
            }
        }
    }

    private void exportVertices(PrintWriter out, Graph<V, E> g) {
        boolean exportVertexLabels = this.parameters.contains((Object)Parameter.EXPORT_VERTEX_LABELS);
        boolean exportCustomVertexAttributes = this.parameters.contains((Object)Parameter.EXPORT_CUSTOM_VERTEX_ATTRIBUTES);
        for (Object from : g.vertexSet()) {
            out.println("\tnode");
            out.println("\t[");
            out.println("\t\tid " + this.getVertexId(from));
            if (exportVertexLabels) {
                String label = this.getVertexAttribute(from, LABEL_ATTRIBUTE_KEY).map(Attribute::getValue).orElse(from.toString());
                out.println("\t\tlabel " + this.quoted(label));
            }
            if (exportCustomVertexAttributes) {
                this.getVertexAttributes(from).ifPresent(vertexAttributes -> vertexAttributes.entrySet().stream().forEach(e -> {
                    String customAttributeKey = (String)e.getKey();
                    Attribute customAttributeValue = (Attribute)e.getValue();
                    if (FORBIDDEN_VERTEX_CUSTOM_ATTRIBUTE_KEYS.contains(customAttributeKey)) {
                        throw new IllegalArgumentException("Key " + customAttributeKey + " is reserved");
                    }
                    if (LABEL_ATTRIBUTE_KEY.equals(customAttributeKey) && exportVertexLabels) {
                        return;
                    }
                    this.exportAttribute(out, customAttributeKey, customAttributeValue);
                }));
            }
            out.println("\t]");
        }
    }

    private void exportEdges(PrintWriter out, Graph<V, E> g) {
        boolean exportEdgeWeights = this.parameters.contains((Object)Parameter.EXPORT_EDGE_WEIGHTS);
        boolean exportEdgeLabels = this.parameters.contains((Object)Parameter.EXPORT_EDGE_LABELS);
        boolean exportCustomEdgeAttributes = this.parameters.contains((Object)Parameter.EXPORT_CUSTOM_EDGE_ATTRIBUTES);
        for (Object edge : g.edgeSet()) {
            out.println("\tedge");
            out.println("\t[");
            this.getEdgeId(edge).ifPresent(eId -> out.println("\t\tid " + eId));
            String s = this.getVertexId(g.getEdgeSource(edge));
            out.println("\t\tsource " + s);
            String t = this.getVertexId(g.getEdgeTarget(edge));
            out.println("\t\ttarget " + t);
            if (exportEdgeLabels) {
                Attribute label = this.getEdgeAttribute(edge, LABEL_ATTRIBUTE_KEY).orElse(DefaultAttribute.createAttribute(edge.toString()));
                this.exportAttribute(out, LABEL_ATTRIBUTE_KEY, label);
            }
            if (exportEdgeWeights && g.getType().isWeighted()) {
                this.exportAttribute(out, WEIGHT_ATTRIBUTE_KEY, DefaultAttribute.createAttribute(g.getEdgeWeight(edge)));
            }
            if (exportCustomEdgeAttributes) {
                this.getEdgeAttributes(edge).ifPresent(edgeAttributes -> edgeAttributes.entrySet().stream().forEach(e -> {
                    String customAttributeKey = (String)e.getKey();
                    Attribute customAttributeValue = (Attribute)e.getValue();
                    if (FORBIDDEN_EDGE_CUSTOM_ATTRIBUTE_KEYS.contains(customAttributeKey)) {
                        throw new IllegalArgumentException("Key " + customAttributeKey + " is reserved");
                    }
                    if (LABEL_ATTRIBUTE_KEY.equals(customAttributeKey) && exportEdgeLabels) {
                        return;
                    }
                    if (WEIGHT_ATTRIBUTE_KEY.equals(customAttributeKey) && exportEdgeWeights) {
                        return;
                    }
                    this.exportAttribute(out, customAttributeKey, customAttributeValue);
                }));
            }
            out.println("\t]");
        }
    }

    public static enum Parameter {
        EXPORT_EDGE_LABELS,
        EXPORT_EDGE_WEIGHTS,
        EXPORT_CUSTOM_EDGE_ATTRIBUTES,
        EXPORT_VERTEX_LABELS,
        EXPORT_CUSTOM_VERTEX_ATTRIBUTES,
        ESCAPE_STRINGS_AS_JAVA;

    }
}

