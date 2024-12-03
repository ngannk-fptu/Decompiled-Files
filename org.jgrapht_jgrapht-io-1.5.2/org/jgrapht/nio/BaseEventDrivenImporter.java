/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jgrapht.alg.util.Pair
 */
package org.jgrapht.nio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.ImportEvent;

public abstract class BaseEventDrivenImporter<V, E> {
    private List<Consumer<Integer>> vertexCountConsumers = new ArrayList<Consumer<Integer>>();
    private List<Consumer<Integer>> edgeCountConsumers = new ArrayList<Consumer<Integer>>();
    private List<Consumer<V>> vertexConsumers = new ArrayList<Consumer<V>>();
    private List<BiConsumer<V, Map<String, Attribute>>> vertexWithAttributesConsumers = new ArrayList<BiConsumer<V, Map<String, Attribute>>>();
    private List<Consumer<E>> edgeConsumers = new ArrayList<Consumer<E>>();
    private List<BiConsumer<E, Map<String, Attribute>>> edgeWithAttributesConsumers = new ArrayList<BiConsumer<E, Map<String, Attribute>>>();
    private List<BiConsumer<String, Attribute>> graphAttributeConsumers = new ArrayList<BiConsumer<String, Attribute>>();
    private List<BiConsumer<Pair<V, String>, Attribute>> vertexAttributeConsumers = new ArrayList<BiConsumer<Pair<V, String>, Attribute>>();
    private List<BiConsumer<Pair<E, String>, Attribute>> edgeAttributeConsumers = new ArrayList<BiConsumer<Pair<E, String>, Attribute>>();
    private List<Consumer<ImportEvent>> importEventConsumers = new ArrayList<Consumer<ImportEvent>>();

    public void addImportEventConsumer(Consumer<ImportEvent> consumer) {
        this.importEventConsumers.add(consumer);
    }

    public void removeImportEventConsumer(Consumer<ImportEvent> consumer) {
        this.importEventConsumers.remove(consumer);
    }

    public void addVertexCountConsumer(Consumer<Integer> consumer) {
        this.vertexCountConsumers.add(consumer);
    }

    public void removeVertexCountConsumer(Consumer<Integer> consumer) {
        this.vertexCountConsumers.remove(consumer);
    }

    public void addEdgeCountConsumer(Consumer<Integer> consumer) {
        this.edgeCountConsumers.add(consumer);
    }

    public void removeEdgeCountConsumer(Consumer<Integer> consumer) {
        this.edgeCountConsumers.remove(consumer);
    }

    public void addVertexConsumer(Consumer<V> consumer) {
        this.vertexConsumers.add(consumer);
    }

    public void removeVertexConsumer(Consumer<V> consumer) {
        this.vertexConsumers.remove(consumer);
    }

    public void addEdgeConsumer(Consumer<E> consumer) {
        this.edgeConsumers.add(consumer);
    }

    public void removeEdgeConsumer(Consumer<E> consumer) {
        this.edgeConsumers.remove(consumer);
    }

    public void addGraphAttributeConsumer(BiConsumer<String, Attribute> consumer) {
        this.graphAttributeConsumers.add(consumer);
    }

    public void removeGraphAttributeConsumer(BiConsumer<String, Attribute> consumer) {
        this.graphAttributeConsumers.remove(consumer);
    }

    public void addVertexAttributeConsumer(BiConsumer<Pair<V, String>, Attribute> consumer) {
        this.vertexAttributeConsumers.add(consumer);
    }

    public void removeVertexAttributeConsumer(BiConsumer<Pair<V, String>, Attribute> consumer) {
        this.vertexAttributeConsumers.remove(consumer);
    }

    public void addVertexWithAttributesConsumer(BiConsumer<V, Map<String, Attribute>> consumer) {
        this.vertexWithAttributesConsumers.add(consumer);
    }

    public void removeVertexWithAttributesConsumer(BiConsumer<V, Map<String, Attribute>> consumer) {
        this.vertexWithAttributesConsumers.remove(consumer);
    }

    public void addEdgeAttributeConsumer(BiConsumer<Pair<E, String>, Attribute> consumer) {
        this.edgeAttributeConsumers.add(consumer);
    }

    public void removeEdgeAttributeConsumer(BiConsumer<Pair<E, String>, Attribute> consumer) {
        this.edgeAttributeConsumers.remove(consumer);
    }

    public void addEdgeWithAttributesConsumer(BiConsumer<E, Map<String, Attribute>> consumer) {
        this.edgeWithAttributesConsumers.add(consumer);
    }

    public void removeEdgeWithAttributesConsumer(BiConsumer<E, Map<String, Attribute>> consumer) {
        this.edgeWithAttributesConsumers.remove(consumer);
    }

    protected void notifyVertexCount(Integer vertexCount) {
        this.vertexCountConsumers.forEach(c -> c.accept(vertexCount));
    }

    protected void notifyEdgeCount(Integer edgeCount) {
        this.edgeCountConsumers.forEach(c -> c.accept(edgeCount));
    }

    protected void notifyVertex(V v) {
        this.vertexConsumers.forEach(c -> c.accept(v));
    }

    protected void notifyVertexWithAttributes(V v, Map<String, Attribute> attrs) {
        this.vertexWithAttributesConsumers.forEach(c -> c.accept(v, attrs));
    }

    protected void notifyEdge(E e) {
        this.edgeConsumers.forEach(c -> c.accept(e));
    }

    protected void notifyEdgeWithAttributes(E e, Map<String, Attribute> attrs) {
        this.edgeWithAttributesConsumers.forEach(c -> c.accept(e, attrs));
    }

    protected void notifyGraphAttribute(String key, Attribute value) {
        this.graphAttributeConsumers.forEach(c -> c.accept(key, value));
    }

    protected void notifyVertexAttribute(V v, String key, Attribute value) {
        this.vertexAttributeConsumers.forEach(c -> c.accept(Pair.of((Object)v, (Object)key), value));
    }

    protected void notifyEdgeAttribute(E e, String key, Attribute value) {
        this.edgeAttributeConsumers.forEach(c -> c.accept(Pair.of((Object)e, (Object)key), value));
    }

    protected void notifyImportEvent(ImportEvent importEvent) {
        this.importEventConsumers.forEach(c -> c.accept(importEvent));
    }
}

