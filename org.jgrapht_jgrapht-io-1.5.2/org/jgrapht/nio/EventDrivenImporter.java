/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Pair
 */
package org.jgrapht.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.ImportEvent;
import org.jgrapht.nio.ImportException;

public interface EventDrivenImporter<V, E> {
    public void addImportEventConsumer(Consumer<ImportEvent> var1);

    public void removeImportEventConsumer(Consumer<ImportEvent> var1);

    public void addVertexCountConsumer(Consumer<Integer> var1);

    public void removeVertexCountConsumer(Consumer<Integer> var1);

    public void addEdgeCountConsumer(Consumer<Integer> var1);

    public void removeEdgeCountConsumer(Consumer<Integer> var1);

    public void addVertexConsumer(Consumer<V> var1);

    public void removeVertexConsumer(Consumer<V> var1);

    public void addVertexWithAttributesConsumer(BiConsumer<V, Map<String, Attribute>> var1);

    public void removeVertexWithAttributesConsumer(BiConsumer<V, Map<String, Attribute>> var1);

    public void addEdgeConsumer(Consumer<E> var1);

    public void removeEdgeConsumer(Consumer<E> var1);

    public void addEdgeWithAttributesConsumer(BiConsumer<E, Map<String, Attribute>> var1);

    public void removeEdgeWithAttributesConsumer(BiConsumer<E, Map<String, Attribute>> var1);

    public void addGraphAttributeConsumer(BiConsumer<String, Attribute> var1);

    public void removeGraphAttributeConsumer(BiConsumer<String, Attribute> var1);

    public void addVertexAttributeConsumer(BiConsumer<Pair<V, String>, Attribute> var1);

    public void removeVertexAttributeConsumer(BiConsumer<Pair<V, String>, Attribute> var1);

    public void addEdgeAttributeConsumer(BiConsumer<Pair<E, String>, Attribute> var1);

    public void removeEdgeAttributeConsumer(BiConsumer<Pair<E, String>, Attribute> var1);

    public void importInput(Reader var1);

    default public void importInput(InputStream in) {
        this.importInput(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    default public void importInput(File file) {
        try {
            this.importInput(new InputStreamReader((InputStream)new FileInputStream(file), StandardCharsets.UTF_8));
        }
        catch (IOException e) {
            throw new ImportException(e);
        }
    }
}

