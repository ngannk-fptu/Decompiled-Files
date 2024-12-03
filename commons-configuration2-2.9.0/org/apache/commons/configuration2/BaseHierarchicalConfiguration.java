/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 */
package org.apache.commons.configuration2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.AbstractHierarchicalConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableHierarchicalConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.interpol.ConfigurationInterpolator;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.InMemoryNodeModelSupport;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.NodeSelector;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.QueryResult;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.apache.commons.configuration2.tree.TrackedNodeModel;
import org.apache.commons.lang3.ObjectUtils;

public class BaseHierarchicalConfiguration
extends AbstractHierarchicalConfiguration<ImmutableNode>
implements InMemoryNodeModelSupport {
    private final EventListener<ConfigurationEvent> changeListener = this.createChangeListener();

    public BaseHierarchicalConfiguration() {
        this((HierarchicalConfiguration<ImmutableNode>)null);
    }

    public BaseHierarchicalConfiguration(HierarchicalConfiguration<ImmutableNode> c) {
        this(BaseHierarchicalConfiguration.createNodeModel(c));
    }

    protected BaseHierarchicalConfiguration(NodeModel<ImmutableNode> model) {
        super(model);
    }

    @Override
    public InMemoryNodeModel getNodeModel() {
        return (InMemoryNodeModel)super.getNodeModel();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Configuration subset(String prefix) {
        this.beginRead(false);
        try {
            List<QueryResult<ImmutableNode>> results = this.fetchNodeList(prefix);
            if (results.isEmpty()) {
                BaseHierarchicalConfiguration baseHierarchicalConfiguration = new BaseHierarchicalConfiguration();
                return baseHierarchicalConfiguration;
            }
            final BaseHierarchicalConfiguration parent = this;
            BaseHierarchicalConfiguration result = new BaseHierarchicalConfiguration(){

                @Override
                protected Object interpolate(Object value) {
                    return parent.interpolate(value);
                }

                @Override
                public ConfigurationInterpolator getInterpolator() {
                    return parent.getInterpolator();
                }
            };
            result.getModel().setRootNode(this.createSubsetRootNode(results));
            if (result.isEmpty()) {
                BaseHierarchicalConfiguration baseHierarchicalConfiguration = new BaseHierarchicalConfiguration();
                return baseHierarchicalConfiguration;
            }
            result.setSynchronizer(this.getSynchronizer());
            BaseHierarchicalConfiguration baseHierarchicalConfiguration = result;
            return baseHierarchicalConfiguration;
        }
        finally {
            this.endRead();
        }
    }

    private ImmutableNode createSubsetRootNode(Collection<QueryResult<ImmutableNode>> results) {
        ImmutableNode.Builder builder = new ImmutableNode.Builder();
        Object value = null;
        int valueCount = 0;
        for (QueryResult<ImmutableNode> result : results) {
            if (result.isAttributeResult()) {
                builder.addAttribute(result.getAttributeName(), result.getAttributeValue(this.getModel().getNodeHandler()));
                continue;
            }
            if (result.getNode().getValue() != null) {
                value = result.getNode().getValue();
                ++valueCount;
            }
            builder.addChildren(result.getNode().getChildren());
            builder.addAttributes(result.getNode().getAttributes());
        }
        if (valueCount == 1) {
            builder.value(value);
        }
        return builder.create();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(String key, boolean supportUpdates) {
        this.beginRead(false);
        try {
            BaseHierarchicalConfiguration baseHierarchicalConfiguration = supportUpdates ? this.createConnectedSubConfiguration(key) : this.createIndependentSubConfiguration(key);
            return baseHierarchicalConfiguration;
        }
        finally {
            this.endRead();
        }
    }

    protected InMemoryNodeModel getSubConfigurationParentModel() {
        return (InMemoryNodeModel)this.getModel();
    }

    protected NodeSelector getSubConfigurationNodeSelector(String key) {
        return new NodeSelector(key);
    }

    protected SubnodeConfiguration createSubConfigurationForTrackedNode(NodeSelector selector, InMemoryNodeModelSupport parentModelSupport) {
        SubnodeConfiguration subConfig = new SubnodeConfiguration(this, new TrackedNodeModel(parentModelSupport, selector, true));
        this.initSubConfigurationForThisParent(subConfig);
        return subConfig;
    }

    protected void initSubConfigurationForThisParent(SubnodeConfiguration subConfig) {
        this.initSubConfiguration(subConfig);
        subConfig.addEventListener(ConfigurationEvent.ANY, this.changeListener);
    }

    private BaseHierarchicalConfiguration createConnectedSubConfiguration(String key) {
        NodeSelector selector = this.getSubConfigurationNodeSelector(key);
        this.getSubConfigurationParentModel().trackNode(selector, this);
        return this.createSubConfigurationForTrackedNode(selector, this);
    }

    private List<HierarchicalConfiguration<ImmutableNode>> createConnectedSubConfigurations(InMemoryNodeModelSupport parentModelSupport, Collection<NodeSelector> selectors) {
        return selectors.stream().map(sel -> this.createSubConfigurationForTrackedNode((NodeSelector)sel, parentModelSupport)).collect(Collectors.toList());
    }

    private BaseHierarchicalConfiguration createIndependentSubConfiguration(String key) {
        List<ImmutableNode> targetNodes = this.fetchFilteredNodeResults(key);
        int size = targetNodes.size();
        if (size != 1) {
            throw new ConfigurationRuntimeException("Passed in key must select exactly one node (found %,d): %s", size, key);
        }
        BaseHierarchicalConfiguration sub = new BaseHierarchicalConfiguration(new InMemoryNodeModel(targetNodes.get(0)));
        this.initSubConfiguration(sub);
        return sub;
    }

    private BaseHierarchicalConfiguration createIndependentSubConfigurationForNode(ImmutableNode node) {
        BaseHierarchicalConfiguration sub = new BaseHierarchicalConfiguration(new InMemoryNodeModel(node));
        this.initSubConfiguration(sub);
        return sub;
    }

    private List<ImmutableNode> fetchFilteredNodeResults(String key) {
        NodeHandler handler = this.getModel().getNodeHandler();
        return this.resolveNodeKey((ImmutableNode)handler.getRootNode(), key, handler);
    }

    @Override
    public ImmutableHierarchicalConfiguration immutableConfigurationAt(String key, boolean supportUpdates) {
        return ConfigurationUtils.unmodifiableConfiguration(this.configurationAt(key, supportUpdates));
    }

    @Override
    public HierarchicalConfiguration<ImmutableNode> configurationAt(String key) {
        return this.configurationAt(key, false);
    }

    @Override
    public ImmutableHierarchicalConfiguration immutableConfigurationAt(String key) {
        return ConfigurationUtils.unmodifiableConfiguration(this.configurationAt(key));
    }

    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(String key) {
        List<ImmutableNode> nodes;
        this.beginRead(false);
        try {
            nodes = this.fetchFilteredNodeResults(key);
        }
        finally {
            this.endRead();
        }
        return nodes.stream().map(this::createIndependentSubConfigurationForNode).collect(Collectors.toList());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> configurationsAt(String key, boolean supportUpdates) {
        InMemoryNodeModel parentModel;
        if (!supportUpdates) {
            return this.configurationsAt(key);
        }
        this.beginRead(false);
        try {
            parentModel = this.getSubConfigurationParentModel();
        }
        finally {
            this.endRead();
        }
        Collection<NodeSelector> selectors = parentModel.selectAndTrackNodes(key, this);
        return this.createConnectedSubConfigurations(this, selectors);
    }

    @Override
    public List<ImmutableHierarchicalConfiguration> immutableConfigurationsAt(String key) {
        return BaseHierarchicalConfiguration.toImmutable(this.configurationsAt(key));
    }

    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> childConfigurationsAt(String key) {
        List<ImmutableNode> nodes;
        this.beginRead(false);
        try {
            nodes = this.fetchFilteredNodeResults(key);
        }
        finally {
            this.endRead();
        }
        if (nodes.size() != 1) {
            return Collections.emptyList();
        }
        return nodes.get(0).stream().map(this::createIndependentSubConfigurationForNode).collect(Collectors.toList());
    }

    @Override
    public List<HierarchicalConfiguration<ImmutableNode>> childConfigurationsAt(String key, boolean supportUpdates) {
        if (!supportUpdates) {
            return this.childConfigurationsAt(key);
        }
        InMemoryNodeModel parentModel = this.getSubConfigurationParentModel();
        return this.createConnectedSubConfigurations(this, parentModel.trackChildNodes(key, this));
    }

    @Override
    public List<ImmutableHierarchicalConfiguration> immutableChildConfigurationsAt(String key) {
        return BaseHierarchicalConfiguration.toImmutable(this.childConfigurationsAt(key));
    }

    protected void subnodeConfigurationChanged(ConfigurationEvent event) {
        this.fireEvent(ConfigurationEvent.SUBNODE_CHANGED, null, event, event.isBeforeUpdate());
    }

    private void initSubConfiguration(BaseHierarchicalConfiguration sub) {
        sub.setSynchronizer(this.getSynchronizer());
        sub.setExpressionEngine(this.getExpressionEngine());
        sub.setListDelimiterHandler(this.getListDelimiterHandler());
        sub.setThrowExceptionOnMissing(this.isThrowExceptionOnMissing());
        sub.getInterpolator().setParentInterpolator(this.getInterpolator());
    }

    private EventListener<ConfigurationEvent> createChangeListener() {
        return this::subnodeConfigurationChanged;
    }

    @Override
    public Configuration interpolatedConfiguration() {
        InterpolatedVisitor visitor = new InterpolatedVisitor();
        NodeHandler handler = this.getModel().getNodeHandler();
        NodeTreeWalker.INSTANCE.walkDFS((ImmutableNode)handler.getRootNode(), visitor, handler);
        BaseHierarchicalConfiguration c = (BaseHierarchicalConfiguration)this.clone();
        c.getNodeModel().setRootNode(visitor.getInterpolatedRoot());
        return c;
    }

    @Override
    protected NodeModel<ImmutableNode> cloneNodeModel() {
        return new InMemoryNodeModel((ImmutableNode)this.getModel().getNodeHandler().getRootNode());
    }

    private static List<ImmutableHierarchicalConfiguration> toImmutable(List<? extends HierarchicalConfiguration<?>> subs) {
        return subs.stream().map(ConfigurationUtils::unmodifiableConfiguration).collect(Collectors.toList());
    }

    private static NodeModel<ImmutableNode> createNodeModel(HierarchicalConfiguration<ImmutableNode> c) {
        ImmutableNode root = c != null ? BaseHierarchicalConfiguration.obtainRootNode(c) : null;
        return new InMemoryNodeModel(root);
    }

    private static ImmutableNode obtainRootNode(HierarchicalConfiguration<ImmutableNode> c) {
        return (ImmutableNode)c.getNodeModel().getNodeHandler().getRootNode();
    }

    private class InterpolatedVisitor
    extends ConfigurationNodeVisitorAdapter<ImmutableNode> {
        private final List<ImmutableNode.Builder> builderStack = new LinkedList<ImmutableNode.Builder>();
        private ImmutableNode interpolatedRoot;

        public ImmutableNode getInterpolatedRoot() {
            return this.interpolatedRoot;
        }

        @Override
        public void visitBeforeChildren(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
            if (this.isLeafNode(node, handler)) {
                this.handleLeafNode(node, handler);
            } else {
                ImmutableNode.Builder builder = new ImmutableNode.Builder(handler.getChildrenCount(node, null)).name(handler.nodeName(node)).value(BaseHierarchicalConfiguration.this.interpolate(handler.getValue(node))).addAttributes(this.interpolateAttributes(node, handler));
                this.push(builder);
            }
        }

        @Override
        public void visitAfterChildren(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
            if (!this.isLeafNode(node, handler)) {
                ImmutableNode newNode = this.pop().create();
                this.storeInterpolatedNode(newNode);
            }
        }

        private void push(ImmutableNode.Builder builder) {
            this.builderStack.add(0, builder);
        }

        private ImmutableNode.Builder pop() {
            return this.builderStack.remove(0);
        }

        private ImmutableNode.Builder peek() {
            return this.builderStack.get(0);
        }

        private boolean isLeafNode(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
            return handler.getChildren(node).isEmpty();
        }

        private void handleLeafNode(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
            Object value = BaseHierarchicalConfiguration.this.interpolate(node.getValue());
            HashMap<String, Object> interpolatedAttributes = new HashMap<String, Object>();
            boolean attributeChanged = this.interpolateAttributes(node, handler, interpolatedAttributes);
            ImmutableNode newNode = this.valueChanged(value, handler.getValue(node)) || attributeChanged ? new ImmutableNode.Builder().name(handler.nodeName(node)).value(value).addAttributes(interpolatedAttributes).create() : node;
            this.storeInterpolatedNode(newNode);
        }

        private void storeInterpolatedNode(ImmutableNode node) {
            if (this.builderStack.isEmpty()) {
                this.interpolatedRoot = node;
            } else {
                this.peek().addChild(node);
            }
        }

        private boolean interpolateAttributes(ImmutableNode node, NodeHandler<ImmutableNode> handler, Map<String, Object> interpolatedAttributes) {
            boolean attributeChanged = false;
            for (String attr : handler.getAttributes(node)) {
                Object attrValue = BaseHierarchicalConfiguration.this.interpolate(handler.getAttributeValue(node, attr));
                if (this.valueChanged(attrValue, handler.getAttributeValue(node, attr))) {
                    attributeChanged = true;
                }
                interpolatedAttributes.put(attr, attrValue);
            }
            return attributeChanged;
        }

        private Map<String, Object> interpolateAttributes(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
            HashMap<String, Object> attributes = new HashMap<String, Object>();
            this.interpolateAttributes(node, handler, attributes);
            return attributes;
        }

        private boolean valueChanged(Object interpolatedValue, Object value) {
            return ObjectUtils.notEqual((Object)interpolatedValue, (Object)value);
        }
    }

    protected static abstract class BuilderVisitor
    extends ConfigurationNodeVisitorAdapter<ImmutableNode> {
        protected BuilderVisitor() {
        }

        @Override
        public void visitBeforeChildren(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
            ReferenceNodeHandler refHandler = (ReferenceNodeHandler)handler;
            this.updateNode(node, refHandler);
            this.insertNewChildNodes(node, refHandler);
        }

        protected abstract void insert(ImmutableNode var1, ImmutableNode var2, ImmutableNode var3, ImmutableNode var4, ReferenceNodeHandler var5);

        protected abstract void update(ImmutableNode var1, Object var2, ReferenceNodeHandler var3);

        private void updateNode(ImmutableNode node, ReferenceNodeHandler refHandler) {
            Object reference = refHandler.getReference(node);
            if (reference != null) {
                this.update(node, reference, refHandler);
            }
        }

        private void insertNewChildNodes(ImmutableNode node, ReferenceNodeHandler refHandler) {
            LinkedList<ImmutableNode> subNodes = new LinkedList<ImmutableNode>(refHandler.getChildren(node));
            Iterator children = subNodes.iterator();
            ImmutableNode nd = null;
            while (children.hasNext()) {
                ImmutableNode sibling1;
                do {
                    sibling1 = nd;
                } while (refHandler.getReference(nd = (ImmutableNode)children.next()) != null && children.hasNext());
                if (refHandler.getReference(nd) != null) continue;
                LinkedList<ImmutableNode> newNodes = new LinkedList<ImmutableNode>();
                newNodes.add(nd);
                while (children.hasNext() && refHandler.getReference(nd = (ImmutableNode)children.next()) == null) {
                    newNodes.add(nd);
                }
                ImmutableNode sibling2 = refHandler.getReference(nd) == null ? null : nd;
                for (ImmutableNode insertNode : newNodes) {
                    if (refHandler.getReference(insertNode) != null) continue;
                    this.insert(insertNode, node, sibling1, sibling2, refHandler);
                    sibling1 = insertNode;
                }
            }
        }
    }
}

