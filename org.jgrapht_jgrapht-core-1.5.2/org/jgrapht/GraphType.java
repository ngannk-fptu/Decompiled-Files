/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht;

public interface GraphType {
    public boolean isDirected();

    public boolean isUndirected();

    public boolean isMixed();

    public boolean isAllowingMultipleEdges();

    public boolean isAllowingSelfLoops();

    public boolean isAllowingCycles();

    public boolean isWeighted();

    public boolean isSimple();

    public boolean isPseudograph();

    public boolean isMultigraph();

    public boolean isModifiable();

    public GraphType asDirected();

    public GraphType asUndirected();

    public GraphType asMixed();

    public GraphType asUnweighted();

    public GraphType asWeighted();

    public GraphType asModifiable();

    public GraphType asUnmodifiable();
}

