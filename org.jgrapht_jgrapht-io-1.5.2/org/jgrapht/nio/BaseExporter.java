/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.nio;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jgrapht.nio.Attribute;

public abstract class BaseExporter<V, E> {
    protected Optional<Supplier<String>> graphIdProvider;
    protected Optional<Supplier<Map<String, Attribute>>> graphAttributeProvider;
    protected Function<V, String> vertexIdProvider;
    protected Optional<Function<V, Map<String, Attribute>>> vertexAttributeProvider;
    protected Optional<Function<E, String>> edgeIdProvider;
    protected Optional<Function<E, Map<String, Attribute>>> edgeAttributeProvider;

    public BaseExporter(Function<V, String> vertexIdProvider) {
        this.vertexIdProvider = Objects.requireNonNull(vertexIdProvider);
        this.graphIdProvider = Optional.empty();
        this.graphAttributeProvider = Optional.empty();
        this.vertexAttributeProvider = Optional.empty();
        this.edgeIdProvider = Optional.empty();
        this.edgeAttributeProvider = Optional.empty();
    }

    public Optional<Supplier<String>> getGraphIdProvider() {
        return this.graphIdProvider;
    }

    public void setGraphIdProvider(Supplier<String> graphIdProvider) {
        this.graphIdProvider = Optional.ofNullable(graphIdProvider);
    }

    public Optional<Supplier<Map<String, Attribute>>> getGraphAttributeProvider() {
        return this.graphAttributeProvider;
    }

    public void setGraphAttributeProvider(Supplier<Map<String, Attribute>> graphAttributeProvider) {
        this.graphAttributeProvider = Optional.ofNullable(graphAttributeProvider);
    }

    public Function<V, String> getVertexIdProvider() {
        return this.vertexIdProvider;
    }

    public void setVertexIdProvider(Function<V, String> vertexIdProvider) {
        this.vertexIdProvider = Objects.requireNonNull(vertexIdProvider);
    }

    public Optional<Function<V, Map<String, Attribute>>> getVertexAttributeProvider() {
        return this.vertexAttributeProvider;
    }

    public void setVertexAttributeProvider(Function<V, Map<String, Attribute>> vertexAttributeProvider) {
        this.vertexAttributeProvider = Optional.ofNullable(vertexAttributeProvider);
    }

    public Optional<Function<E, String>> getEdgeIdProvider() {
        return this.edgeIdProvider;
    }

    public void setEdgeIdProvider(Function<E, String> edgeIdProvider) {
        this.edgeIdProvider = Optional.ofNullable(edgeIdProvider);
    }

    public Optional<Function<E, Map<String, Attribute>>> getEdgeAttributeProvider() {
        return this.edgeAttributeProvider;
    }

    public void setEdgeAttributeProvider(Function<E, Map<String, Attribute>> edgeAttributeProvider) {
        this.edgeAttributeProvider = Optional.ofNullable(edgeAttributeProvider);
    }

    protected Optional<String> getGraphId() {
        return this.graphIdProvider.map(x -> (String)x.get());
    }

    protected String getVertexId(V v) {
        return this.vertexIdProvider.apply(v);
    }

    protected Optional<String> getEdgeId(E e) {
        return this.edgeIdProvider.map(x -> (String)x.apply(e));
    }

    protected Optional<Map<String, Attribute>> getVertexAttributes(V v) {
        return this.vertexAttributeProvider.map(x -> (Map)x.apply(v));
    }

    protected Optional<Attribute> getVertexAttribute(V v, String key) {
        return this.vertexAttributeProvider.map(x -> (Attribute)((Map)x.apply(v)).get(key));
    }

    protected Optional<Map<String, Attribute>> getEdgeAttributes(E e) {
        return this.edgeAttributeProvider.map(x -> (Map)x.apply(e));
    }

    protected Optional<Attribute> getEdgeAttribute(E e, String key) {
        return this.edgeAttributeProvider.map(x -> (Attribute)((Map)x.apply(e)).get(key));
    }

    protected Optional<Attribute> getGraphAttribute(String key) {
        return this.graphAttributeProvider.map(x -> (Attribute)((Map)x.get()).get(key));
    }
}

