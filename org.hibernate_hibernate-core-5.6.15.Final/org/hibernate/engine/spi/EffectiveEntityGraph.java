/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.Map;
import org.hibernate.Incubating;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.spi.AppliedGraph;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.jboss.logging.Logger;

public class EffectiveEntityGraph
implements AppliedGraph,
Serializable {
    private static final Logger log = Logger.getLogger(EffectiveEntityGraph.class);
    private final boolean allowOverwrite;
    private GraphSemantic semantic;
    private RootGraphImplementor<?> graph;

    @Incubating
    public EffectiveEntityGraph() {
        this(false);
    }

    @Incubating
    public EffectiveEntityGraph(boolean allowOverwrite) {
        this.allowOverwrite = allowOverwrite;
    }

    @Override
    public GraphSemantic getSemantic() {
        return this.semantic;
    }

    @Override
    public RootGraphImplementor<?> getGraph() {
        return this.graph;
    }

    public void applyGraph(RootGraphImplementor<?> graph, GraphSemantic semantic) {
        if (semantic == null) {
            throw new IllegalArgumentException("Graph semantic cannot be null");
        }
        this.verifyWriteability();
        log.debugf("Setting effective graph state [%s] : %s", (Object)semantic.name(), graph);
        this.semantic = semantic;
        this.graph = graph;
    }

    private void verifyWriteability() {
        if (!this.allowOverwrite && this.semantic != null) {
            throw new IllegalStateException("Cannot overwrite existing state, should clear previous state first");
        }
    }

    public void applyConfiguredGraph(Map<String, ?> properties) {
        if (properties == null || properties.isEmpty()) {
            return;
        }
        RootGraphImplementor fetchHint = (RootGraphImplementor)properties.get(GraphSemantic.FETCH.getJpaHintName());
        RootGraphImplementor loadHint = (RootGraphImplementor)properties.get(GraphSemantic.LOAD.getJpaHintName());
        if (fetchHint == null) {
            fetchHint = (RootGraphImplementor)properties.get(GraphSemantic.FETCH.getJakartaJpaHintName());
        }
        if (loadHint == null) {
            loadHint = (RootGraphImplementor)properties.get(GraphSemantic.LOAD.getJakartaJpaHintName());
        }
        if (fetchHint == null && loadHint == null) {
            log.debugf("Neither LOAD nor FETCH graph were found in properties", new Object[0]);
            return;
        }
        if (fetchHint != null) {
            if (loadHint != null) {
                throw new IllegalArgumentException("Passed properties contained both a LOAD and a FETCH graph which is illegal - only one should be passed");
            }
            this.applyGraph(fetchHint, GraphSemantic.FETCH);
        } else {
            this.applyGraph(loadHint, GraphSemantic.LOAD);
        }
    }

    public void clear() {
        this.semantic = null;
        this.graph = null;
    }
}

