/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.util.Iterator;
import org.apache.jackrabbit.spi.ChildInfo;
import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.PropertyId;

public interface NodeInfo
extends ItemInfo {
    @Override
    public NodeId getId();

    public int getIndex();

    public Name getNodetype();

    public Name[] getMixins();

    public PropertyId[] getReferences();

    public Iterator<PropertyId> getPropertyIds();

    public Iterator<ChildInfo> getChildInfos();
}

