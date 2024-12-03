/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import org.apache.jackrabbit.spi.ItemInfo;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;

public abstract class ItemInfoImpl
implements ItemInfo,
Serializable {
    private final Path path;
    private final boolean isNode;

    public ItemInfoImpl(NodeId parentId, Name name, Path path, boolean isNode) {
        this(path, isNode);
    }

    public ItemInfoImpl(Path path, boolean isNode) {
        this.path = path;
        this.isNode = isNode;
    }

    @Override
    public boolean denotesNode() {
        return this.isNode;
    }

    @Override
    public Path getPath() {
        return this.path;
    }
}

