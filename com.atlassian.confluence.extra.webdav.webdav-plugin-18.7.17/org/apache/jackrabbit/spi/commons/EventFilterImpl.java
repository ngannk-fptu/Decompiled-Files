/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Event;
import org.apache.jackrabbit.spi.EventFilter;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.Path;

public class EventFilterImpl
implements EventFilter,
Serializable {
    private final int eventTypes;
    private final boolean isDeep;
    private final Path absPath;
    private final Set<String> uuids;
    private final Set<Name> nodeTypeNames;
    private final boolean noLocal;

    public EventFilterImpl(int eventTypes, Path absPath, boolean isDeep, String[] uuids, Set<Name> nodeTypeNames, boolean noLocal) {
        this.eventTypes = eventTypes;
        this.absPath = absPath;
        this.isDeep = isDeep;
        this.uuids = uuids != null ? new HashSet<String>(Arrays.asList(uuids)) : null;
        this.nodeTypeNames = nodeTypeNames != null ? new HashSet<Name>(nodeTypeNames) : null;
        this.noLocal = noLocal;
    }

    @Override
    public boolean accept(Event event, boolean isLocal) {
        int type = event.getType();
        if ((type & this.eventTypes) == 0) {
            return false;
        }
        if (isLocal && this.noLocal) {
            return false;
        }
        if (event.getType() == 64) {
            return true;
        }
        NodeId parentId = event.getParentId();
        if (this.uuids != null) {
            if (parentId.getPath() == null) {
                if (!this.uuids.contains(parentId.getUniqueID())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (this.nodeTypeNames != null) {
            HashSet<Name> eventTypes = new HashSet<Name>();
            eventTypes.addAll(Arrays.asList(event.getMixinTypeNames()));
            eventTypes.add(event.getPrimaryNodeTypeName());
            eventTypes.retainAll(this.nodeTypeNames);
            if (eventTypes.isEmpty()) {
                return false;
            }
        }
        try {
            Path eventPath = event.getPath().getAncestor(1);
            boolean match = eventPath.equals(this.absPath);
            if (!match && this.isDeep) {
                match = eventPath.isDescendantOf(this.absPath);
            }
            return match;
        }
        catch (RepositoryException repositoryException) {
            return false;
        }
    }

    public int getEventTypes() {
        return this.eventTypes;
    }

    public boolean isDeep() {
        return this.isDeep;
    }

    public Path getAbsPath() {
        return this.absPath;
    }

    public String[] getUUIDs() {
        if (this.uuids == null) {
            return null;
        }
        return this.uuids.toArray(new String[this.uuids.size()]);
    }

    public Set<Name> getNodeTypeNames() {
        if (this.nodeTypeNames == null) {
            return null;
        }
        return Collections.unmodifiableSet(this.nodeTypeNames);
    }

    public boolean getNoLocal() {
        return this.noLocal;
    }

    public String toString() {
        return new StringBuffer(this.getClass().getName()).append("[").append("eventTypes: ").append(this.eventTypes).append(", ").append("absPath: ").append(this.absPath).append(", ").append("isDeep: ").append(this.isDeep).append(", ").append("uuids: ").append(this.uuids).append(", ").append("nodeTypeNames: ").append(this.nodeTypeNames).append(", ").append("noLocal: ").append(this.noLocal).append("]").toString();
    }
}

