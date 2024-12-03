/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.jgrapht.Graph
 *  org.jgrapht.Graphs
 *  org.jgrapht.alg.cycle.CycleDetector
 *  org.jgrapht.graph.AsSubgraph
 *  org.jgrapht.graph.builder.GraphTypeBuilder
 *  org.jgrapht.nio.Attribute
 *  org.jgrapht.nio.DefaultAttribute
 *  org.jgrapht.nio.dot.DOTExporter
 */
package com.atlassian.plugin.webresource.graph;

import com.atlassian.plugin.webresource.graph.DependencyEdge;
import com.atlassian.plugin.webresource.graph.DependencyGraphBuilder;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.cycle.CycleDetector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

public class DependencyGraph<V> {
    private static final String SOURCE_KEY_MANDATORY_MESSAGE = "The source vertex key is mandatory.";
    private final DOTExporter<V, DependencyEdge> exporter;
    private final Graph<V, DependencyEdge> resourceGraph;
    private final Class<V> verticeClazz;

    public DependencyGraph(@Nonnull Class<V> verticeClazz) {
        this(verticeClazz, DependencyGraph.createGraph(verticeClazz));
    }

    public DependencyGraph(@Nonnull Class<V> verticeClazz, @Nonnull Graph<V, DependencyEdge> resourceGraph) {
        this.verticeClazz = Objects.requireNonNull(verticeClazz, "The vertice class type is mandatory.");
        this.resourceGraph = Objects.requireNonNull(resourceGraph, "The resource graph is mandatory.");
        this.exporter = new DOTExporter();
        this.exporter.setVertexAttributeProvider(vertex -> {
            LinkedHashMap<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("label", DefaultAttribute.createAttribute((String)vertex.toString()));
            return map;
        });
    }

    private static <V> Graph<V, DependencyEdge> createGraph(Class<V> verticeClazz) {
        return GraphTypeBuilder.directed().allowingMultipleEdges(true).vertexClass(verticeClazz).edgeClass(DependencyEdge.class).allowingSelfLoops(true).buildGraph();
    }

    @Nonnull
    public static DependencyGraphBuilder builder() {
        return new DependencyGraphBuilder();
    }

    public boolean equals(@Nullable Object other) {
        if (other instanceof DependencyGraph) {
            DependencyGraph otherResourceGraph = (DependencyGraph)other;
            return this.resourceGraph.edgeSet().equals(otherResourceGraph.resourceGraph.edgeSet());
        }
        return false;
    }

    @Nonnull
    public DependencyGraph<V> findCyclicSubGraphByVertex(@Nonnull V sourceKey) {
        Objects.requireNonNull(sourceKey, SOURCE_KEY_MANDATORY_MESSAGE);
        CycleDetector cycleDetector = new CycleDetector(this.resourceGraph);
        Set cycles = cycleDetector.findCyclesContainingVertex(sourceKey);
        return new DependencyGraph<V>(this.verticeClazz, new AsSubgraph(this.resourceGraph, cycles));
    }

    @Nonnull
    public DependencyGraph<V> findDependantsSubGraphByKey(@Nonnull V sourceKey) {
        Objects.requireNonNull(sourceKey, SOURCE_KEY_MANDATORY_MESSAGE);
        Set<V> dependants = this.findAllDependantsByKey(sourceKey);
        return new DependencyGraph<V>(this.verticeClazz, new AsSubgraph(this.resourceGraph, dependants));
    }

    @Nonnull
    public DependencyGraph<V> findDependencySubGraphByRequestableKey(@Nonnull V sourceKey) {
        Objects.requireNonNull(sourceKey, SOURCE_KEY_MANDATORY_MESSAGE);
        Set<V> dependencies = this.findAllDependenciesByKey(sourceKey);
        return new DependencyGraph<V>(this.verticeClazz, new AsSubgraph(this.resourceGraph, dependencies));
    }

    @Nonnull
    public DependencyGraph<V> findIntersectionSubGraph(@Nonnull V sourceKey, @Nonnull V targetKey) {
        Objects.requireNonNull(sourceKey, SOURCE_KEY_MANDATORY_MESSAGE);
        Objects.requireNonNull(targetKey, "The target requestable key is mandatory.");
        Set<V> sourceDependants = this.findAllDependantsByKey(sourceKey);
        Set<V> targetDependants = this.findAllDependantsByKey(targetKey);
        HashSet<V> commonDependants = new HashSet<V>(sourceDependants);
        commonDependants.retainAll(targetDependants);
        return new DependencyGraph<V>(this.verticeClazz, new AsSubgraph(this.resourceGraph, commonDependants));
    }

    public int hashCode() {
        return this.resourceGraph.hashCode();
    }

    public String toString() {
        StringWriter writer = new StringWriter();
        this.exporter.exportGraph(this.resourceGraph, (Writer)writer);
        return ((Object)writer).toString();
    }

    public Collection<DependencyEdge> getEdges() {
        return this.resourceGraph.edgeSet();
    }

    public boolean hasDependency(V key) {
        return this.resourceGraph.containsVertex(key);
    }

    void addDependencies(@Nonnull V sourceKey, @Nonnull Collection<V> dependencyKeys) {
        Objects.requireNonNull(sourceKey, SOURCE_KEY_MANDATORY_MESSAGE);
        Objects.requireNonNull(dependencyKeys, "The dependency keys are mandatory.");
        for (V dependencyKey : dependencyKeys) {
            this.addDependency(sourceKey, dependencyKey);
        }
    }

    void addDependency(@Nonnull V sourceKey, @Nonnull V dependencyKey) {
        Objects.requireNonNull(sourceKey, SOURCE_KEY_MANDATORY_MESSAGE);
        Objects.requireNonNull(dependencyKey, "The dependency key is mandatory.");
        this.resourceGraph.addVertex(sourceKey);
        this.resourceGraph.addVertex(dependencyKey);
        DependencyEdge<V> dependency = new DependencyEdge<V>();
        dependency.setSource(sourceKey);
        dependency.setTarget(dependencyKey);
        this.resourceGraph.addEdge(sourceKey, dependencyKey, dependency);
    }

    void merge(@Nonnull DependencyGraph<V> sourceGraph) {
        Objects.requireNonNull(sourceGraph, "The graph to be merged is mandatory.");
        Graphs.addGraph(this.resourceGraph, sourceGraph.resourceGraph);
    }

    @Nonnull
    Collection<V> toVertexes() {
        return new HashSet(this.resourceGraph.vertexSet());
    }

    private Set<V> findAllDependantsByKey(V sourceKey) {
        HashSet dependants = new HashSet();
        this.findDependantsSubGraphByKey(dependants, sourceKey);
        return dependants;
    }

    private Set<V> findAllDependenciesByKey(V sourceKey) {
        HashSet dependencies = new HashSet();
        this.findDependencySubGraphByRequestableKey(dependencies, sourceKey);
        return dependencies;
    }

    private void findDependantsSubGraphByKey(Collection<V> resources, V sourceKey) {
        if (resources.contains(sourceKey)) {
            return;
        }
        resources.add(sourceKey);
        for (Object predecessorRequestable : Graphs.predecessorListOf(this.resourceGraph, sourceKey)) {
            this.findDependantsSubGraphByKey(resources, predecessorRequestable);
        }
    }

    private void findDependencySubGraphByRequestableKey(Collection<V> resources, V sourceKey) {
        if (resources.contains(sourceKey)) {
            return;
        }
        resources.add(sourceKey);
        for (Object successorRequestable : Graphs.successorListOf(this.resourceGraph, sourceKey)) {
            this.findDependencySubGraphByRequestableKey(resources, successorRequestable);
        }
    }
}

