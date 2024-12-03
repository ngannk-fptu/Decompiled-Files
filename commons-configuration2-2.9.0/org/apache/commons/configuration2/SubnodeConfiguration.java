/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.InMemoryNodeModelSupport;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.NodeSelector;
import org.apache.commons.configuration2.tree.TrackedNodeModel;

public class SubnodeConfiguration
extends BaseHierarchicalConfiguration {
    private final BaseHierarchicalConfiguration parent;
    private final NodeSelector rootSelector;

    public SubnodeConfiguration(BaseHierarchicalConfiguration parent, TrackedNodeModel model) {
        super(model);
        if (parent == null) {
            throw new IllegalArgumentException("Parent configuration must not be null!");
        }
        if (model == null) {
            throw new IllegalArgumentException("Node model must not be null!");
        }
        this.parent = parent;
        this.rootSelector = model.getSelector();
    }

    public BaseHierarchicalConfiguration getParent() {
        return this.parent;
    }

    public NodeSelector getRootSelector() {
        return this.rootSelector;
    }

    public void close() {
        this.getTrackedModel().close();
    }

    @Override
    public InMemoryNodeModel getNodeModel() {
        ImmutableNode root = this.getParent().getNodeModel().getTrackedNode(this.getRootSelector());
        return new InMemoryNodeModel(root);
    }

    public InMemoryNodeModel getRootNodeModel() {
        if (this.getParent() instanceof SubnodeConfiguration) {
            return ((SubnodeConfiguration)this.getParent()).getRootNodeModel();
        }
        return this.getParent().getNodeModel();
    }

    @Override
    protected NodeModel<ImmutableNode> cloneNodeModel() {
        InMemoryNodeModel parentModel = (InMemoryNodeModel)this.getParent().getModel();
        parentModel.trackNode(this.getRootSelector(), this.getParent());
        return new TrackedNodeModel(this.getParent(), this.getRootSelector(), true);
    }

    @Override
    protected NodeSelector getSubConfigurationNodeSelector(String key) {
        return this.getRootSelector().subSelector(key);
    }

    @Override
    protected InMemoryNodeModel getSubConfigurationParentModel() {
        return this.getTrackedModel().getParentModel();
    }

    @Override
    protected SubnodeConfiguration createSubConfigurationForTrackedNode(NodeSelector selector, InMemoryNodeModelSupport parentModelSupport) {
        return super.createSubConfigurationForTrackedNode(selector, this.getParent());
    }

    private TrackedNodeModel getTrackedModel() {
        return (TrackedNodeModel)this.getModel();
    }
}

