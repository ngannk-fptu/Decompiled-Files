/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration2.tree.ImmutableNode;

class ReferenceTracker {
    private final Map<ImmutableNode, Object> references;
    private final List<Object> removedReferences;

    private ReferenceTracker(Map<ImmutableNode, Object> refs, List<Object> removedRefs) {
        this.references = refs;
        this.removedReferences = removedRefs;
    }

    public ReferenceTracker() {
        this(Collections.emptyMap(), Collections.emptyList());
    }

    public ReferenceTracker addReferences(Map<ImmutableNode, ?> refs) {
        HashMap<ImmutableNode, Object> newRefs = new HashMap<ImmutableNode, Object>(this.references);
        newRefs.putAll(refs);
        return new ReferenceTracker(newRefs, this.removedReferences);
    }

    public ReferenceTracker updateReferences(Map<ImmutableNode, ImmutableNode> replacedNodes, Collection<ImmutableNode> removedNodes) {
        if (!this.references.isEmpty()) {
            HashMap<ImmutableNode, Object> newRefs = null;
            for (Map.Entry<ImmutableNode, ImmutableNode> e : replacedNodes.entrySet()) {
                Object ref = this.references.get(e.getKey());
                if (ref == null) continue;
                if (newRefs == null) {
                    newRefs = new HashMap<ImmutableNode, Object>(this.references);
                }
                newRefs.put(e.getValue(), ref);
                newRefs.remove(e.getKey());
            }
            LinkedList<Object> newRemovedRefs = newRefs != null ? new LinkedList<Object>(this.removedReferences) : null;
            for (ImmutableNode node : removedNodes) {
                Object ref = this.references.get(node);
                if (ref == null) continue;
                if (newRefs == null) {
                    newRefs = new HashMap<ImmutableNode, Object>(this.references);
                }
                newRefs.remove(node);
                if (newRemovedRefs == null) {
                    newRemovedRefs = new LinkedList<Object>(this.removedReferences);
                }
                newRemovedRefs.add(ref);
            }
            if (newRefs != null) {
                return new ReferenceTracker(newRefs, newRemovedRefs);
            }
        }
        return this;
    }

    public Object getReference(ImmutableNode node) {
        return this.references.get(node);
    }

    public List<Object> getRemovedReferences() {
        return Collections.unmodifiableList(this.removedReferences);
    }
}

