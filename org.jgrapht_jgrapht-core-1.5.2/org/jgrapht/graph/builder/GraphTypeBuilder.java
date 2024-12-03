/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph.builder;

import java.util.function.Supplier;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.graph.DirectedWeightedPseudograph;
import org.jgrapht.graph.Multigraph;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.WeightedMultigraph;
import org.jgrapht.graph.WeightedPseudograph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.util.SupplierUtil;
import org.jgrapht.util.TypeUtil;

public final class GraphTypeBuilder<V, E> {
    private boolean undirected;
    private boolean directed;
    private boolean weighted;
    private boolean allowingMultipleEdges;
    private boolean allowingSelfLoops;
    private Supplier<V> vertexSupplier;
    private Supplier<E> edgeSupplier;

    private GraphTypeBuilder(boolean directed, boolean undirected) {
        this.directed = directed;
        this.undirected = undirected;
        this.weighted = false;
        this.allowingMultipleEdges = false;
        this.allowingSelfLoops = false;
    }

    public static <V, E> GraphTypeBuilder<V, E> directed() {
        return new GraphTypeBuilder<V, E>(true, false);
    }

    public static <V, E> GraphTypeBuilder<V, E> undirected() {
        return new GraphTypeBuilder<V, E>(false, true);
    }

    public static <V, E> GraphTypeBuilder<V, E> mixed() {
        return new GraphTypeBuilder<V, E>(true, true);
    }

    public static <V, E> GraphTypeBuilder<V, E> forGraphType(GraphType type) {
        GraphTypeBuilder<V, E> builder = new GraphTypeBuilder<V, E>(type.isDirected() || type.isMixed(), type.isUndirected() || type.isMixed());
        builder.weighted = type.isWeighted();
        builder.allowingSelfLoops = type.isAllowingSelfLoops();
        builder.allowingMultipleEdges = type.isAllowingMultipleEdges();
        return builder;
    }

    public static <V, E> GraphTypeBuilder<V, E> forGraph(Graph<V, E> graph) {
        GraphTypeBuilder<V, E> builder = GraphTypeBuilder.forGraphType(graph.getType());
        builder.vertexSupplier = graph.getVertexSupplier();
        builder.edgeSupplier = graph.getEdgeSupplier();
        return builder;
    }

    public GraphTypeBuilder<V, E> weighted(boolean weighted) {
        this.weighted = weighted;
        return this;
    }

    public GraphTypeBuilder<V, E> allowingSelfLoops(boolean allowingSelfLoops) {
        this.allowingSelfLoops = allowingSelfLoops;
        return this;
    }

    public GraphTypeBuilder<V, E> allowingMultipleEdges(boolean allowingMultipleEdges) {
        this.allowingMultipleEdges = allowingMultipleEdges;
        return this;
    }

    public <V1 extends V> GraphTypeBuilder<V1, E> vertexSupplier(Supplier<V1> vertexSupplier) {
        GraphTypeBuilder newBuilder = (GraphTypeBuilder)TypeUtil.uncheckedCast(this);
        newBuilder.vertexSupplier = vertexSupplier;
        return newBuilder;
    }

    public <E1 extends E> GraphTypeBuilder<V, E1> edgeSupplier(Supplier<E1> edgeSupplier) {
        GraphTypeBuilder newBuilder = (GraphTypeBuilder)TypeUtil.uncheckedCast(this);
        newBuilder.edgeSupplier = edgeSupplier;
        return newBuilder;
    }

    public <V1 extends V> GraphTypeBuilder<V1, E> vertexClass(Class<V1> vertexClass) {
        GraphTypeBuilder newBuilder = (GraphTypeBuilder)TypeUtil.uncheckedCast(this);
        newBuilder.vertexSupplier = SupplierUtil.createSupplier(vertexClass);
        return newBuilder;
    }

    public <E1 extends E> GraphTypeBuilder<V, E1> edgeClass(Class<E1> edgeClass) {
        GraphTypeBuilder newBuilder = (GraphTypeBuilder)TypeUtil.uncheckedCast(this);
        newBuilder.edgeSupplier = SupplierUtil.createSupplier(edgeClass);
        return newBuilder;
    }

    public GraphType buildType() {
        DefaultGraphType.Builder typeBuilder = new DefaultGraphType.Builder();
        if (this.directed && this.undirected) {
            typeBuilder = typeBuilder.mixed();
        } else if (this.directed) {
            typeBuilder = typeBuilder.directed();
        } else if (this.undirected) {
            typeBuilder = typeBuilder.undirected();
        }
        return typeBuilder.allowMultipleEdges(this.allowingMultipleEdges).allowSelfLoops(this.allowingSelfLoops).weighted(this.weighted).build();
    }

    public GraphBuilder<V, E, Graph<V, E>> buildGraphBuilder() {
        return new GraphBuilder(this.buildGraph());
    }

    public Graph<V, E> buildGraph() {
        if (this.directed && this.undirected) {
            throw new UnsupportedOperationException("Mixed graphs are not supported");
        }
        if (this.directed) {
            if (this.allowingSelfLoops && this.allowingMultipleEdges) {
                if (this.weighted) {
                    return new DirectedWeightedPseudograph<V, E>(this.vertexSupplier, this.edgeSupplier);
                }
                return new DirectedPseudograph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
            }
            if (this.allowingMultipleEdges) {
                if (this.weighted) {
                    return new DirectedWeightedMultigraph<V, E>(this.vertexSupplier, this.edgeSupplier);
                }
                return new DirectedMultigraph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
            }
            if (this.allowingSelfLoops) {
                if (this.weighted) {
                    return new DefaultDirectedWeightedGraph<V, E>(this.vertexSupplier, this.edgeSupplier);
                }
                return new DefaultDirectedGraph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
            }
            if (this.weighted) {
                return new SimpleDirectedWeightedGraph<V, E>(this.vertexSupplier, this.edgeSupplier);
            }
            return new SimpleDirectedGraph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
        }
        if (this.allowingSelfLoops && this.allowingMultipleEdges) {
            if (this.weighted) {
                return new WeightedPseudograph<V, E>(this.vertexSupplier, this.edgeSupplier);
            }
            return new Pseudograph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
        }
        if (this.allowingMultipleEdges) {
            if (this.weighted) {
                return new WeightedMultigraph<V, E>(this.vertexSupplier, this.edgeSupplier);
            }
            return new Multigraph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
        }
        if (this.allowingSelfLoops) {
            if (this.weighted) {
                return new DefaultUndirectedWeightedGraph<V, E>(this.vertexSupplier, this.edgeSupplier);
            }
            return new DefaultUndirectedGraph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
        }
        if (this.weighted) {
            return new SimpleWeightedGraph<V, E>(this.vertexSupplier, this.edgeSupplier);
        }
        return new SimpleGraph<V, E>(this.vertexSupplier, this.edgeSupplier, false);
    }
}

