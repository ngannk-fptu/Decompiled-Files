/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import org.apache.jackrabbit.spi.Event;
import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.QValue;

public class EventImpl
implements Event,
Serializable {
    private final int type;
    private final Path path;
    private final ItemId itemId;
    private final NodeId parentId;
    private final Name primaryNodeTypeName;
    private final Name[] mixinTypeNames;
    private final String userId;
    private final String userData;
    private final long timestamp;
    private final Map<Name, QValue> info;

    public EventImpl(int type, Path path, ItemId itemId, NodeId parentId, Name primaryNodeTypeName, Name[] mixinTypeNames, String userId) {
        this(type, path, itemId, parentId, primaryNodeTypeName, mixinTypeNames, userId, null, Long.MIN_VALUE, Collections.EMPTY_MAP);
    }

    public EventImpl(int type, Path path, ItemId itemId, NodeId parentId, Name primaryNodeTypeName, Name[] mixinTypeNames, String userId, String userData, long timestamp, Map<Name, QValue> info) {
        this.type = type;
        this.path = path;
        this.itemId = itemId;
        this.parentId = parentId;
        this.primaryNodeTypeName = primaryNodeTypeName;
        this.mixinTypeNames = mixinTypeNames;
        this.userId = userId;
        this.userData = userData;
        this.info = new HashMap<Name, QValue>(info);
        this.timestamp = timestamp;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @Override
    public ItemId getItemId() {
        return this.itemId;
    }

    @Override
    public NodeId getParentId() {
        return this.parentId;
    }

    @Override
    public Name getPrimaryNodeTypeName() {
        return this.primaryNodeTypeName;
    }

    @Override
    public Name[] getMixinTypeNames() {
        Name[] mixins = new Name[this.mixinTypeNames.length];
        System.arraycopy(this.mixinTypeNames, 0, mixins, 0, this.mixinTypeNames.length);
        return mixins;
    }

    @Override
    public String getUserID() {
        return this.userId;
    }

    @Override
    public Map<Name, QValue> getInfo() throws RepositoryException {
        return this.info;
    }

    @Override
    public String getUserData() {
        return this.userData;
    }

    @Override
    public long getDate() throws RepositoryException {
        if (this.timestamp == Long.MIN_VALUE) {
            throw new UnsupportedRepositoryOperationException("Event.getDate() not supported");
        }
        return this.timestamp;
    }

    public String toString() {
        return new StringBuffer(this.getClass().getName()).append("[").append("eventTypes: ").append(this.type).append(", ").append("absPath: ").append(this.path).append(", ").append("itemId: ").append(this.itemId).append(", ").append("parentId: ").append(this.parentId).append(", ").append("primaryNodeTypeName: ").append(this.primaryNodeTypeName).append(", ").append("mixinTypeNames: ").append(Arrays.toString(this.mixinTypeNames)).append(", ").append("userId").append(this.userId).append("]").toString();
    }
}

