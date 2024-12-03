/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.jgrapht.Graph;
import org.jgrapht.nio.ExportException;

public interface GraphExporter<V, E> {
    default public void exportGraph(Graph<V, E> g, OutputStream out) {
        this.exportGraph(g, new OutputStreamWriter(out, StandardCharsets.UTF_8));
    }

    public void exportGraph(Graph<V, E> var1, Writer var2);

    default public void exportGraph(Graph<V, E> g, File file) {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);){
            this.exportGraph(g, writer);
        }
        catch (IOException e) {
            throw new ExportException(e);
        }
    }
}

