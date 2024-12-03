/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.InMemoryNodeModelSupport;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.NodeSelector;
import org.apache.commons.configuration2.tree.QueryResult;

public class TrackedNodeModel
implements NodeModel<ImmutableNode> {
    private final InMemoryNodeModelSupport parentModelSupport;
    private final NodeSelector selector;
    private final boolean releaseTrackedNodeOnFinalize;
    private final AtomicBoolean closed;

    public TrackedNodeModel(InMemoryNodeModelSupport modelSupport, NodeSelector sel, boolean untrackOnFinalize) {
        if (modelSupport == null) {
            throw new IllegalArgumentException("Underlying model support must not be null!");
        }
        if (sel == null) {
            throw new IllegalArgumentException("Selector must not be null!");
        }
        this.parentModelSupport = modelSupport;
        this.selector = sel;
        this.releaseTrackedNodeOnFinalize = untrackOnFinalize;
        this.closed = new AtomicBoolean();
    }

    public InMemoryNodeModelSupport getParentModelSupport() {
        return this.parentModelSupport;
    }

    public InMemoryNodeModel getParentModel() {
        return this.getParentModelSupport().getNodeModel();
    }

    public NodeSelector getSelector() {
        return this.selector;
    }

    public boolean isReleaseTrackedNodeOnFinalize() {
        return this.releaseTrackedNodeOnFinalize;
    }

    @Override
    public void setRootNode(ImmutableNode newRoot) {
        this.getParentModel().replaceTrackedNode(this.getSelector(), newRoot);
    }

    @Override
    public NodeHandler<ImmutableNode> getNodeHandler() {
        return this.getParentModel().getTrackedNodeHandler(this.getSelector());
    }

    @Override
    public void addProperty(String key, Iterable<?> values, NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().addProperty(key, this.getSelector(), values, resolver);
    }

    @Override
    public void addNodes(String key, Collection<? extends ImmutableNode> nodes, NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().addNodes(key, this.getSelector(), nodes, resolver);
    }

    @Override
    public void setProperty(String key, Object value, NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().setProperty(key, this.getSelector(), value, resolver);
    }

    public List<QueryResult<ImmutableNode>> clearTree(String key, NodeKeyResolver<ImmutableNode> resolver) {
        return this.getParentModel().clearTree(key, this.getSelector(), resolver);
    }

    @Override
    public void clearProperty(String key, NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().clearProperty(key, this.getSelector(), resolver);
    }

    @Override
    public void clear(NodeKeyResolver<ImmutableNode> resolver) {
        this.getParentModel().clearTree(null, this.getSelector(), resolver);
    }

    @Override
    public ImmutableNode getInMemoryRepresentation() {
        return this.getNodeHandler().getRootNode();
    }

    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.getParentModel().untrackNode(this.getSelector());
        }
    }

    protected void finalize() throws Throwable {
        if (this.isReleaseTrackedNodeOnFinalize()) {
            this.close();
        }
        super.finalize();
    }
}

