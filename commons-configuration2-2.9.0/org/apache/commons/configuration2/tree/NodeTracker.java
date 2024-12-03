/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.NodeSelector;

class NodeTracker {
    private final Map<NodeSelector, TrackedNodeData> trackedNodes;

    public NodeTracker() {
        this(Collections.emptyMap());
    }

    private NodeTracker(Map<NodeSelector, TrackedNodeData> map) {
        this.trackedNodes = map;
    }

    public NodeTracker trackNode(ImmutableNode root, NodeSelector selector, NodeKeyResolver<ImmutableNode> resolver, NodeHandler<ImmutableNode> handler) {
        HashMap<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        TrackedNodeData trackData = (TrackedNodeData)newState.get(selector);
        newState.put(selector, NodeTracker.trackDataForAddedObserver(root, selector, resolver, handler, trackData));
        return new NodeTracker(newState);
    }

    public NodeTracker trackNodes(Collection<NodeSelector> selectors, Collection<ImmutableNode> nodes) {
        HashMap<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        Iterator<ImmutableNode> itNodes = nodes.iterator();
        selectors.forEach(selector -> {
            ImmutableNode node = (ImmutableNode)itNodes.next();
            TrackedNodeData trackData = (TrackedNodeData)newState.get(selector);
            trackData = trackData == null ? new TrackedNodeData(node) : trackData.observerAdded();
            newState.put((NodeSelector)selector, trackData);
        });
        return new NodeTracker(newState);
    }

    public NodeTracker untrackNode(NodeSelector selector) {
        TrackedNodeData trackData = this.getTrackedNodeData(selector);
        HashMap<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        TrackedNodeData newTrackData = trackData.observerRemoved();
        if (newTrackData == null) {
            newState.remove(selector);
        } else {
            newState.put(selector, newTrackData);
        }
        return new NodeTracker(newState);
    }

    public ImmutableNode getTrackedNode(NodeSelector selector) {
        return this.getTrackedNodeData(selector).getNode();
    }

    public boolean isTrackedNodeDetached(NodeSelector selector) {
        return this.getTrackedNodeData(selector).isDetached();
    }

    public InMemoryNodeModel getDetachedNodeModel(NodeSelector selector) {
        return this.getTrackedNodeData(selector).getDetachedModel();
    }

    public NodeTracker update(ImmutableNode root, NodeSelector txTarget, NodeKeyResolver<ImmutableNode> resolver, NodeHandler<ImmutableNode> handler) {
        if (this.trackedNodes.isEmpty()) {
            return this;
        }
        HashMap<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>();
        this.trackedNodes.entrySet().forEach(e -> newState.put((NodeSelector)e.getKey(), NodeTracker.determineUpdatedTrackedNodeData(root, txTarget, resolver, handler, e)));
        return new NodeTracker(newState);
    }

    public NodeTracker detachAllTrackedNodes() {
        if (this.trackedNodes.isEmpty()) {
            return this;
        }
        return new NodeTracker(this.trackedNodes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((TrackedNodeData)e.getValue()).isDetached() ? (TrackedNodeData)e.getValue() : ((TrackedNodeData)e.getValue()).detach(null))));
    }

    public NodeTracker replaceAndDetachTrackedNode(NodeSelector selector, ImmutableNode newNode) {
        HashMap<NodeSelector, TrackedNodeData> newState = new HashMap<NodeSelector, TrackedNodeData>(this.trackedNodes);
        newState.put(selector, this.getTrackedNodeData(selector).detach(newNode));
        return new NodeTracker(newState);
    }

    private TrackedNodeData getTrackedNodeData(NodeSelector selector) {
        TrackedNodeData trackData = this.trackedNodes.get(selector);
        if (trackData == null) {
            throw new ConfigurationRuntimeException("No tracked node found: " + selector);
        }
        return trackData;
    }

    private static TrackedNodeData determineUpdatedTrackedNodeData(ImmutableNode root, NodeSelector txTarget, NodeKeyResolver<ImmutableNode> resolver, NodeHandler<ImmutableNode> handler, Map.Entry<NodeSelector, TrackedNodeData> e) {
        ImmutableNode newTarget;
        if (e.getValue().isDetached()) {
            return e.getValue();
        }
        try {
            newTarget = e.getKey().select(root, resolver, handler);
        }
        catch (Exception ex) {
            newTarget = null;
        }
        if (newTarget == null) {
            return NodeTracker.detachedTrackedNodeData(txTarget, e);
        }
        return e.getValue().updateNode(newTarget);
    }

    private static TrackedNodeData detachedTrackedNodeData(NodeSelector txTarget, Map.Entry<NodeSelector, TrackedNodeData> e) {
        ImmutableNode newNode = e.getKey().equals(txTarget) ? NodeTracker.createEmptyTrackedNode(e.getValue()) : null;
        return e.getValue().detach(newNode);
    }

    private static ImmutableNode createEmptyTrackedNode(TrackedNodeData data) {
        return new ImmutableNode.Builder().name(data.getNode().getNodeName()).create();
    }

    private static TrackedNodeData trackDataForAddedObserver(ImmutableNode root, NodeSelector selector, NodeKeyResolver<ImmutableNode> resolver, NodeHandler<ImmutableNode> handler, TrackedNodeData trackData) {
        if (trackData != null) {
            return trackData.observerAdded();
        }
        ImmutableNode target = selector.select(root, resolver, handler);
        if (target == null) {
            throw new ConfigurationRuntimeException("Selector does not select unique node: " + selector);
        }
        return new TrackedNodeData(target);
    }

    private static class TrackedNodeData {
        private final ImmutableNode node;
        private final int observerCount;
        private final InMemoryNodeModel detachedModel;

        public TrackedNodeData(ImmutableNode nd) {
            this(nd, 1, null);
        }

        private TrackedNodeData(ImmutableNode nd, int obsCount, InMemoryNodeModel detachedNodeModel) {
            this.node = nd;
            this.observerCount = obsCount;
            this.detachedModel = detachedNodeModel;
        }

        public ImmutableNode getNode() {
            return this.getDetachedModel() != null ? this.getDetachedModel().getRootNode() : this.node;
        }

        public InMemoryNodeModel getDetachedModel() {
            return this.detachedModel;
        }

        public boolean isDetached() {
            return this.getDetachedModel() != null;
        }

        public TrackedNodeData observerAdded() {
            return new TrackedNodeData(this.node, this.observerCount + 1, this.getDetachedModel());
        }

        public TrackedNodeData observerRemoved() {
            return this.observerCount <= 1 ? null : new TrackedNodeData(this.node, this.observerCount - 1, this.getDetachedModel());
        }

        public TrackedNodeData updateNode(ImmutableNode newNode) {
            return new TrackedNodeData(newNode, this.observerCount, this.getDetachedModel());
        }

        public TrackedNodeData detach(ImmutableNode newNode) {
            ImmutableNode newTrackedNode = newNode != null ? newNode : this.getNode();
            return new TrackedNodeData(newTrackedNode, this.observerCount, new InMemoryNodeModel(newTrackedNode));
        }
    }
}

