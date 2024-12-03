/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.List;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.tree.ExpressionEngine;

public interface ImmutableHierarchicalConfiguration
extends ImmutableConfiguration {
    public ExpressionEngine getExpressionEngine();

    public int getMaxIndex(String var1);

    public String getRootElementName();

    public ImmutableHierarchicalConfiguration immutableConfigurationAt(String var1, boolean var2);

    public ImmutableHierarchicalConfiguration immutableConfigurationAt(String var1);

    public List<ImmutableHierarchicalConfiguration> immutableConfigurationsAt(String var1);

    public List<ImmutableHierarchicalConfiguration> immutableChildConfigurationsAt(String var1);
}

