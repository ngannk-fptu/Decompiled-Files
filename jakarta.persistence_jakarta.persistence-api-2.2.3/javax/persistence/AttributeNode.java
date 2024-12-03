/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.Map;
import javax.persistence.Subgraph;

public interface AttributeNode<T> {
    public String getAttributeName();

    public Map<Class, Subgraph> getSubgraphs();

    public Map<Class, Subgraph> getKeySubgraphs();
}

