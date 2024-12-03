/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.Collection;
import java.util.List;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.NodeModelSupport;

public interface HierarchicalConfiguration<T>
extends Configuration,
ImmutableHierarchicalConfiguration,
NodeModelSupport<T> {
    public void setExpressionEngine(ExpressionEngine var1);

    public void addNodes(String var1, Collection<? extends T> var2);

    public HierarchicalConfiguration<T> configurationAt(String var1, boolean var2);

    public HierarchicalConfiguration<T> configurationAt(String var1);

    public List<HierarchicalConfiguration<T>> configurationsAt(String var1);

    public List<HierarchicalConfiguration<T>> configurationsAt(String var1, boolean var2);

    public List<HierarchicalConfiguration<T>> childConfigurationsAt(String var1);

    public List<HierarchicalConfiguration<T>> childConfigurationsAt(String var1, boolean var2);

    public void clearTree(String var1);
}

