/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio.csv;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.function.Function;
import org.jgrapht.Graph;
import org.jgrapht.nio.BaseExporter;
import org.jgrapht.nio.GraphExporter;
import org.jgrapht.nio.IntegerIdProvider;

public class VisioExporter<V, E>
extends BaseExporter<V, E>
implements GraphExporter<V, E> {
    public VisioExporter() {
        this(new IntegerIdProvider());
    }

    public VisioExporter(Function<V, String> vertexIdProvider) {
        super(vertexIdProvider);
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) {
        PrintWriter out = new PrintWriter(writer);
        for (Object v : g.vertexSet()) {
            this.exportVertex(out, v);
        }
        for (Object e : g.edgeSet()) {
            this.exportEdge(out, e, g);
        }
        out.flush();
    }

    private void exportEdge(PrintWriter out, E edge, Graph<V, E> g) {
        String sourceName = this.getVertexId(g.getEdgeSource(edge));
        String targetName = this.getVertexId(g.getEdgeTarget(edge));
        out.print("Link,");
        out.print(sourceName);
        out.print("-->");
        out.print(targetName);
        out.print(",,,");
        out.print(sourceName);
        out.print(",");
        out.print(targetName);
        out.print("\n");
    }

    private void exportVertex(PrintWriter out, V vertex) {
        String name = this.getVertexId(vertex);
        out.print("Shape,");
        out.print(name);
        out.print(",,");
        out.print(name);
        out.print("\n");
    }
}

