/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.PropertyId;

public interface IdFactory {
    public PropertyId createPropertyId(NodeId var1, Name var2);

    public NodeId createNodeId(NodeId var1, Path var2);

    public NodeId createNodeId(String var1, Path var2);

    public NodeId createNodeId(String var1);

    public String toJcrIdentifier(NodeId var1);

    public NodeId fromJcrIdentifier(String var1);
}

