/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import java.util.Map;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QValue;

public interface Event {
    public static final int NODE_ADDED = 1;
    public static final int NODE_REMOVED = 2;
    public static final int PROPERTY_ADDED = 4;
    public static final int PROPERTY_REMOVED = 8;
    public static final int PROPERTY_CHANGED = 16;
    public static final int NODE_MOVED = 32;
    public static final int PERSIST = 64;
    public static final int ALL_TYPES = 127;

    public int getType();

    public Path getPath();

    public ItemId getItemId();

    public NodeId getParentId();

    public Name getPrimaryNodeTypeName();

    public Name[] getMixinTypeNames();

    public String getUserID();

    public Map<Name, QValue> getInfo() throws RepositoryException;

    public String getUserData();

    public long getDate() throws RepositoryException;
}

