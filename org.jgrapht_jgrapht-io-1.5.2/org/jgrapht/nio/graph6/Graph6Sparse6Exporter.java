/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 *  org.jgrapht.GraphTests
 */
package org.jgrapht.nio.graph6;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.nio.ExportException;
import org.jgrapht.nio.GraphExporter;

public class Graph6Sparse6Exporter<V, E>
implements GraphExporter<V, E> {
    private Format format;
    private ByteArrayOutputStream byteArrayOutputStream;
    public static final Format DEFAULT_GRAPH6SPARSE6_FORMAT = Format.GRAPH6;
    private byte currentByte;
    private int bitIndex;

    public Graph6Sparse6Exporter() {
        this(DEFAULT_GRAPH6SPARSE6_FORMAT);
    }

    public Graph6Sparse6Exporter(Format format) {
        this.format = Objects.requireNonNull(format, "Format cannot be null");
    }

    @Override
    public void exportGraph(Graph<V, E> g, Writer writer) throws ExportException {
        GraphTests.requireUndirected(g);
        if (this.format == Format.GRAPH6 && !GraphTests.isSimple(g)) {
            throw new ExportException("Graphs exported in graph6 format cannot contain loops or multiple edges.");
        }
        ArrayList vertices = new ArrayList(g.vertexSet());
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.currentByte = 0;
        this.bitIndex = 0;
        try {
            if (this.format == Format.SPARSE6) {
                this.writeSparse6(g, vertices);
            } else {
                this.writeGraph6(g, vertices);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String g6 = "";
        try {
            g6 = this.byteArrayOutputStream.toString("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        PrintWriter out = new PrintWriter(writer);
        out.print(g6);
        out.flush();
    }

    private void writeSparse6(Graph<V, E> g, List<V> vertices) throws IOException {
        int[][] edges = new int[g.edgeSet().size()][2];
        int index = 0;
        for (int j = 0; j < vertices.size(); ++j) {
            for (int i = 0; i <= j; ++i) {
                if (!g.containsEdge(vertices.get(i), vertices.get(j))) continue;
                for (int p = 0; p < g.getAllEdges(vertices.get(i), vertices.get(j)).size(); ++p) {
                    edges[index][0] = i;
                    edges[index][1] = j;
                    ++index;
                }
            }
        }
        this.byteArrayOutputStream.write(":".getBytes());
        this.writeNumberOfVertices(vertices.size());
        int k = (int)Math.ceil(Math.log(vertices.size()) / Math.log(2.0));
        int m = 0;
        int v = 0;
        while (m < edges.length) {
            if (edges[m][1] > v + 1) {
                this.writeBit(true);
                this.writeIntInKBits(edges[m][1], k);
                v = edges[m][1];
                continue;
            }
            if (edges[m][1] == v + 1) {
                this.writeBit(true);
                this.writeIntInKBits(edges[m][0], k);
                ++v;
                ++m;
                continue;
            }
            this.writeBit(false);
            this.writeIntInKBits(edges[m][0], k);
            ++m;
        }
        if (this.bitIndex != 0) {
            int padding = 6 - this.bitIndex;
            for (int i = 0; i < padding; ++i) {
                this.writeBit(true);
            }
            this.writeByte();
        }
    }

    private void writeGraph6(Graph<V, E> g, List<V> vertices) throws IOException {
        this.writeNumberOfVertices(vertices.size());
        for (int i = 0; i < vertices.size(); ++i) {
            for (int j = 0; j < i; ++j) {
                this.writeBit(g.containsEdge(vertices.get(i), vertices.get(j)));
            }
        }
        this.writeByte();
    }

    private void writeNumberOfVertices(int n) throws IOException {
        assert (n >= 0);
        if (n <= 62) {
            this.byteArrayOutputStream.write(n + 63);
        } else if (n <= 258047) {
            this.writeIntInKBits(63, 6);
            this.writeIntInKBits(n, 18);
        } else {
            this.writeIntInKBits(63, 6);
            this.writeIntInKBits(63, 6);
            this.writeIntInKBits(n, 36);
        }
    }

    private void writeIntInKBits(int number, int k) {
        for (int i = k - 1; i >= 0; --i) {
            this.writeBit((number & 1 << i) != 0);
        }
    }

    private void writeBit(boolean bit) {
        if (this.bitIndex == 6) {
            this.writeByte();
        }
        if (bit) {
            this.currentByte = (byte)(this.currentByte | 1 << 5 - this.bitIndex);
        }
        ++this.bitIndex;
    }

    private void writeByte() {
        this.byteArrayOutputStream.write(this.currentByte + 63);
        this.currentByte = 0;
        this.bitIndex = 0;
    }

    public static enum Format {
        GRAPH6,
        SPARSE6;

    }
}

