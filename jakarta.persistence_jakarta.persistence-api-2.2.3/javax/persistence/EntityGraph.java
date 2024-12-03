/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

import java.util.List;
import javax.persistence.AttributeNode;
import javax.persistence.Subgraph;
import javax.persistence.metamodel.Attribute;

public interface EntityGraph<T> {
    public String getName();

    public void addAttributeNodes(String ... var1);

    public void addAttributeNodes(Attribute<T, ?> ... var1);

    public <X> Subgraph<X> addSubgraph(Attribute<T, X> var1);

    public <X> Subgraph<? extends X> addSubgraph(Attribute<T, X> var1, Class<? extends X> var2);

    public <X> Subgraph<X> addSubgraph(String var1);

    public <X> Subgraph<X> addSubgraph(String var1, Class<X> var2);

    public <X> Subgraph<X> addKeySubgraph(Attribute<T, X> var1);

    public <X> Subgraph<? extends X> addKeySubgraph(Attribute<T, X> var1, Class<? extends X> var2);

    public <X> Subgraph<X> addKeySubgraph(String var1);

    public <X> Subgraph<X> addKeySubgraph(String var1, Class<X> var2);

    public <T> Subgraph<? extends T> addSubclassSubgraph(Class<? extends T> var1);

    public List<AttributeNode<?>> getAttributeNodes();
}

