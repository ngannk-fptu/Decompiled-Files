/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.graph;

import java.io.Serializable;
import org.jgrapht.GraphType;

public class DefaultGraphType
implements GraphType,
Serializable {
    private static final long serialVersionUID = 4291049312119347474L;
    private final boolean directed;
    private final boolean undirected;
    private final boolean selfLoops;
    private final boolean multipleEdges;
    private final boolean weighted;
    private final boolean allowsCycles;
    private final boolean modifiable;

    private DefaultGraphType(boolean directed, boolean undirected, boolean selfLoops, boolean multipleEdges, boolean weighted, boolean allowsCycles, boolean modifiable) {
        this.directed = directed;
        this.undirected = undirected;
        this.selfLoops = selfLoops;
        this.multipleEdges = multipleEdges;
        this.weighted = weighted;
        this.allowsCycles = allowsCycles;
        this.modifiable = modifiable;
    }

    @Override
    public boolean isDirected() {
        return this.directed && !this.undirected;
    }

    @Override
    public boolean isUndirected() {
        return this.undirected && !this.directed;
    }

    @Override
    public boolean isMixed() {
        return this.undirected && this.directed;
    }

    @Override
    public boolean isAllowingMultipleEdges() {
        return this.multipleEdges;
    }

    @Override
    public boolean isAllowingSelfLoops() {
        return this.selfLoops;
    }

    @Override
    public boolean isWeighted() {
        return this.weighted;
    }

    @Override
    public boolean isAllowingCycles() {
        return this.allowsCycles;
    }

    @Override
    public boolean isModifiable() {
        return this.modifiable;
    }

    @Override
    public boolean isSimple() {
        return !this.isAllowingMultipleEdges() && !this.isAllowingSelfLoops();
    }

    @Override
    public boolean isPseudograph() {
        return this.isAllowingMultipleEdges() && this.isAllowingSelfLoops();
    }

    @Override
    public boolean isMultigraph() {
        return this.isAllowingMultipleEdges() && !this.isAllowingSelfLoops();
    }

    @Override
    public GraphType asDirected() {
        return new Builder(this).directed().build();
    }

    @Override
    public GraphType asUndirected() {
        return new Builder(this).undirected().build();
    }

    @Override
    public GraphType asMixed() {
        return new Builder(this).mixed().build();
    }

    @Override
    public GraphType asUnweighted() {
        return new Builder(this).weighted(false).build();
    }

    @Override
    public GraphType asWeighted() {
        return new Builder(this).weighted(true).build();
    }

    @Override
    public GraphType asModifiable() {
        return new Builder(this).modifiable(true).build();
    }

    @Override
    public GraphType asUnmodifiable() {
        return new Builder(this).modifiable(false).build();
    }

    public static DefaultGraphType simple() {
        return new Builder().undirected().allowSelfLoops(false).allowMultipleEdges(false).weighted(false).build();
    }

    public static DefaultGraphType multigraph() {
        return new Builder().undirected().allowSelfLoops(false).allowMultipleEdges(true).weighted(false).build();
    }

    public static DefaultGraphType pseudograph() {
        return new Builder().undirected().allowSelfLoops(true).allowMultipleEdges(true).weighted(false).build();
    }

    public static DefaultGraphType directedSimple() {
        return new Builder().directed().allowSelfLoops(false).allowMultipleEdges(false).weighted(false).build();
    }

    public static DefaultGraphType directedMultigraph() {
        return new Builder().directed().allowSelfLoops(false).allowMultipleEdges(true).weighted(false).build();
    }

    public static DefaultGraphType directedPseudograph() {
        return new Builder().directed().allowSelfLoops(true).allowMultipleEdges(true).weighted(false).build();
    }

    public static DefaultGraphType mixed() {
        return new Builder().mixed().allowSelfLoops(true).allowMultipleEdges(true).weighted(false).build();
    }

    public static DefaultGraphType dag() {
        return new Builder().directed().allowSelfLoops(false).allowMultipleEdges(true).allowCycles(false).weighted(false).build();
    }

    public String toString() {
        return "DefaultGraphType [directed=" + this.directed + ", undirected=" + this.undirected + ", self-loops=" + this.selfLoops + ", multiple-edges=" + this.multipleEdges + ", weighted=" + this.weighted + ", allows-cycles=" + this.allowsCycles + ", modifiable=" + this.modifiable + "]";
    }

    public static class Builder {
        private boolean directed;
        private boolean undirected;
        private boolean allowSelfLoops;
        private boolean allowMultipleEdges;
        private boolean weighted;
        private boolean allowCycles;
        private boolean modifiable;

        public Builder() {
            this.directed = false;
            this.undirected = true;
            this.allowSelfLoops = true;
            this.allowMultipleEdges = true;
            this.weighted = false;
            this.allowCycles = true;
            this.modifiable = true;
        }

        public Builder(GraphType type) {
            this.directed = type.isDirected() || type.isMixed();
            this.undirected = type.isUndirected() || type.isMixed();
            this.allowSelfLoops = type.isAllowingSelfLoops();
            this.allowMultipleEdges = type.isAllowingMultipleEdges();
            this.weighted = type.isWeighted();
            this.allowCycles = type.isAllowingCycles();
            this.modifiable = type.isModifiable();
        }

        public Builder(boolean directed, boolean undirected) {
            if (!directed && !undirected) {
                throw new IllegalArgumentException("At least one of directed or undirected must be true");
            }
            this.directed = directed;
            this.undirected = undirected;
            this.allowSelfLoops = true;
            this.allowMultipleEdges = true;
            this.weighted = false;
            this.allowCycles = true;
            this.modifiable = true;
        }

        public Builder directed() {
            this.directed = true;
            this.undirected = false;
            return this;
        }

        public Builder undirected() {
            this.directed = false;
            this.undirected = true;
            return this;
        }

        public Builder mixed() {
            this.directed = true;
            this.undirected = true;
            return this;
        }

        public Builder allowSelfLoops(boolean value) {
            this.allowSelfLoops = value;
            return this;
        }

        public Builder allowMultipleEdges(boolean value) {
            this.allowMultipleEdges = value;
            return this;
        }

        public Builder weighted(boolean value) {
            this.weighted = value;
            return this;
        }

        public Builder allowCycles(boolean value) {
            this.allowCycles = value;
            return this;
        }

        public Builder modifiable(boolean value) {
            this.modifiable = value;
            return this;
        }

        public DefaultGraphType build() {
            return new DefaultGraphType(this.directed, this.undirected, this.allowSelfLoops, this.allowMultipleEdges, this.weighted, this.allowCycles, this.modifiable);
        }
    }
}

