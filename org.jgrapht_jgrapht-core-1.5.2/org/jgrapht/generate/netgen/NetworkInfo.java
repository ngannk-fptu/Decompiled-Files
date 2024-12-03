/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.generate.netgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jgrapht.generate.netgen.NetworkGeneratorConfig;

public class NetworkInfo<V, E> {
    NetworkGeneratorConfig config;
    List<V> vertices;
    List<E> skeletonArcs;

    NetworkInfo(NetworkGeneratorConfig config) {
        this.config = config;
        this.vertices = new ArrayList<V>();
        this.skeletonArcs = new ArrayList();
    }

    void registerChainArc(E chainArc) {
        this.skeletonArcs.add(chainArc);
    }

    public List<V> getPureSources() {
        return Collections.unmodifiableList(this.vertices.subList(0, this.config.getPureSourceNum()));
    }

    public List<V> getTransshipmentSources() {
        return Collections.unmodifiableList(this.vertices.subList(this.config.getPureSourceNum(), this.config.getSourceNum()));
    }

    public List<V> getSources() {
        return Collections.unmodifiableList(this.vertices.subList(0, this.config.getSourceNum()));
    }

    public List<V> getTransshipmentNodes() {
        return Collections.unmodifiableList(this.vertices.subList(this.config.getSourceNum(), this.config.getSourceNum() + this.config.getTransshipNodeNum()));
    }

    public List<V> getPureSinks() {
        return Collections.unmodifiableList(this.vertices.subList(this.config.getNodeNum() - this.config.getPureSinkNum(), this.config.getNodeNum()));
    }

    public List<V> getTransshipmentSinks() {
        return Collections.unmodifiableList(this.vertices.subList(this.config.getNodeNum() - this.config.getSinkNum(), this.config.getNodeNum() - this.config.getPureSinkNum()));
    }

    public List<V> getSinks() {
        return Collections.unmodifiableList(this.vertices.subList(this.config.getNodeNum() - this.config.getSinkNum(), this.config.getNodeNum()));
    }

    public List<E> getSkeletonArcs() {
        return Collections.unmodifiableList(this.skeletonArcs);
    }
}

