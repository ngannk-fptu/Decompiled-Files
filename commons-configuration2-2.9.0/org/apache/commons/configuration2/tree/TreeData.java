/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.tree.AbstractImmutableNodeHandler;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeTracker;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.apache.commons.configuration2.tree.ReferenceTracker;

class TreeData
extends AbstractImmutableNodeHandler
implements ReferenceNodeHandler {
    private final ImmutableNode root;
    private final Map<ImmutableNode, ImmutableNode> parentMapping;
    private final Map<ImmutableNode, ImmutableNode> replacementMapping;
    private final Map<ImmutableNode, ImmutableNode> inverseReplacementMapping;
    private final NodeTracker nodeTracker;
    private final ReferenceTracker referenceTracker;

    public TreeData(ImmutableNode root, Map<ImmutableNode, ImmutableNode> parentMapping, Map<ImmutableNode, ImmutableNode> replacements, NodeTracker tracker, ReferenceTracker refTracker) {
        this.root = root;
        this.parentMapping = parentMapping;
        this.replacementMapping = replacements;
        this.inverseReplacementMapping = this.createInverseMapping(replacements);
        this.nodeTracker = tracker;
        this.referenceTracker = refTracker;
    }

    @Override
    public ImmutableNode getRootNode() {
        return this.root;
    }

    public NodeTracker getNodeTracker() {
        return this.nodeTracker;
    }

    public ReferenceTracker getReferenceTracker() {
        return this.referenceTracker;
    }

    @Override
    public ImmutableNode getParent(ImmutableNode node) {
        if (node == this.getRootNode()) {
            return null;
        }
        ImmutableNode org = TreeData.handleReplacements(node, this.inverseReplacementMapping);
        ImmutableNode parent = this.parentMapping.get(org);
        if (parent == null) {
            throw new IllegalArgumentException("Cannot determine parent! " + node + " is not part of this model.");
        }
        return TreeData.handleReplacements(parent, this.replacementMapping);
    }

    public Map<ImmutableNode, ImmutableNode> copyParentMapping() {
        return new HashMap<ImmutableNode, ImmutableNode>(this.parentMapping);
    }

    public Map<ImmutableNode, ImmutableNode> copyReplacementMapping() {
        return new HashMap<ImmutableNode, ImmutableNode>(this.replacementMapping);
    }

    public TreeData updateNodeTracker(NodeTracker newTracker) {
        return new TreeData(this.root, this.parentMapping, this.replacementMapping, newTracker, this.referenceTracker);
    }

    public TreeData updateReferenceTracker(ReferenceTracker newTracker) {
        return new TreeData(this.root, this.parentMapping, this.replacementMapping, this.nodeTracker, newTracker);
    }

    @Override
    public Object getReference(ImmutableNode node) {
        return this.getReferenceTracker().getReference(node);
    }

    @Override
    public List<Object> removedReferences() {
        return this.getReferenceTracker().getRemovedReferences();
    }

    private static ImmutableNode handleReplacements(ImmutableNode replace, Map<ImmutableNode, ImmutableNode> mapping) {
        ImmutableNode org;
        ImmutableNode node = replace;
        do {
            if ((org = mapping.get(node)) == null) continue;
            node = org;
        } while (org != null);
        return node;
    }

    private Map<ImmutableNode, ImmutableNode> createInverseMapping(Map<ImmutableNode, ImmutableNode> replacements) {
        return replacements.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
}

