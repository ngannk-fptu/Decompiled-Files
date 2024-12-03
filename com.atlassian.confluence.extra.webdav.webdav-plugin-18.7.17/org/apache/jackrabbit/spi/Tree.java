/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.QValue;

public interface Tree {
    public Name getName();

    public Name getPrimaryTypeName();

    public String getUniqueId();

    public void addProperty(NodeId var1, Name var2, int var3, QValue var4) throws RepositoryException;

    public void addProperty(NodeId var1, Name var2, int var3, QValue[] var4) throws RepositoryException;

    public Tree addChild(Name var1, Name var2, String var3);
}

