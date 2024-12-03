/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.Graph
 */
package org.jgrapht.nio;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.jgrapht.Graph;
import org.jgrapht.nio.ImportException;

public interface GraphImporter<V, E> {
    default public void importGraph(Graph<V, E> g, InputStream in) {
        this.importGraph(g, new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    public void importGraph(Graph<V, E> var1, Reader var2);

    default public void importGraph(Graph<V, E> g, File file) {
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8);){
            this.importGraph(g, reader);
        }
        catch (IOException e) {
            throw new ImportException(e);
        }
    }
}

