/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.mutable.Mutable
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.ModelTransaction;
import org.apache.commons.configuration2.tree.NodeAddData;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.NodeSelector;
import org.apache.commons.configuration2.tree.NodeTracker;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.NodeUpdateData;
import org.apache.commons.configuration2.tree.QueryResult;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.apache.commons.configuration2.tree.ReferenceTracker;
import org.apache.commons.configuration2.tree.TrackedNodeHandler;
import org.apache.commons.configuration2.tree.TreeData;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

public class InMemoryNodeModel
implements NodeModel<ImmutableNode> {
    private static final NodeHandler<ImmutableNode> DUMMY_HANDLER = new TreeData(null, Collections.emptyMap(), Collections.emptyMap(), null, new ReferenceTracker());
    private final AtomicReference<TreeData> structure;

    public InMemoryNodeModel() {
        this(null);
    }

    public InMemoryNodeModel(ImmutableNode root) {
        this.structure = new AtomicReference<TreeData>(this.createTreeData(InMemoryNodeModel.initialRootNode(root), null));
    }

    public ImmutableNode getRootNode() {
        return this.getTreeData().getRootNode();
    }

    @Override
    public NodeHandler<ImmutableNode> getNodeHandler() {
        return this.getReferenceNodeHandler();
    }

    @Override
    public void addProperty(String key, Iterable<?> values, NodeKeyResolver<ImmutableNode> resolver) {
        this.addProperty(key, null, values, resolver);
    }

    public void addProperty(String key, NodeSelector selector, Iterable<?> values, NodeKeyResolver<ImmutableNode> resolver) {
        if (InMemoryNodeModel.valuesNotEmpty(values)) {
            this.updateModel(tx -> {
                this.initializeAddTransaction(tx, key, values, resolver);
                return true;
            }, selector, resolver);
        }
    }

    @Override
    public void addNodes(String key, Collection<? extends ImmutableNode> nodes, NodeKeyResolver<ImmutableNode> resolver) {
        this.addNodes(key, null, nodes, resolver);
    }

    public void addNodes(String key, NodeSelector selector, Collection<? extends ImmutableNode> nodes, NodeKeyResolver<ImmutableNode> resolver) {
        if (nodes != null && !nodes.isEmpty()) {
            this.updateModel(tx -> {
                List<QueryResult<ImmutableNode>> results = resolver.resolveKey(tx.getQueryRoot(), key, tx.getCurrentData());
                if (results.size() == 1) {
                    if (results.get(0).isAttributeResult()) {
                        throw InMemoryNodeModel.attributeKeyException(key);
                    }
                    tx.addAddNodesOperation(results.get(0).getNode(), nodes);
                } else {
                    NodeAddData<ImmutableNode> addData = resolver.resolveAddKey(tx.getQueryRoot(), key, tx.getCurrentData());
                    if (addData.isAttribute()) {
                        throw InMemoryNodeModel.attributeKeyException(key);
                    }
                    ImmutableNode newNode = new ImmutableNode.Builder(nodes.size()).name(addData.getNewNodeName()).addChildren(nodes).create();
                    InMemoryNodeModel.addNodesByAddData(tx, addData, Collections.singleton(newNode));
                }
                return true;
            }, selector, resolver);
        }
    }

    @Override
    public void setProperty(String key, Object value, NodeKeyResolver<ImmutableNode> resolver) {
        this.setProperty(key, null, value, resolver);
    }

    public void setProperty(String key, NodeSelector selector, Object value, NodeKeyResolver<ImmutableNode> resolver) {
        this.updateModel(tx -> {
            boolean added = false;
            NodeUpdateData<ImmutableNode> updateData = resolver.resolveUpdateKey(tx.getQueryRoot(), key, value, tx.getCurrentData());
            if (!updateData.getNewValues().isEmpty()) {
                this.initializeAddTransaction(tx, key, updateData.getNewValues(), resolver);
                added = true;
            }
            boolean cleared = InMemoryNodeModel.initializeClearTransaction(tx, updateData.getRemovedNodes());
            boolean updated = InMemoryNodeModel.initializeUpdateTransaction(tx, updateData.getChangedValues());
            return added || cleared || updated;
        }, selector, resolver);
    }

    public List<QueryResult<ImmutableNode>> clearTree(String key, NodeKeyResolver<ImmutableNode> resolver) {
        return this.clearTree(key, null, resolver);
    }

    public List<QueryResult<ImmutableNode>> clearTree(String key, NodeSelector selector, NodeKeyResolver<ImmutableNode> resolver) {
        LinkedList<QueryResult<ImmutableNode>> removedElements = new LinkedList<QueryResult<ImmutableNode>>();
        this.updateModel(tx -> {
            boolean changes = false;
            TreeData currentStructure = tx.getCurrentData();
            List<QueryResult<ImmutableNode>> results = resolver.resolveKey(tx.getQueryRoot(), key, currentStructure);
            removedElements.clear();
            removedElements.addAll(results);
            for (QueryResult<ImmutableNode> result : results) {
                if (result.isAttributeResult()) {
                    tx.addRemoveAttributeOperation(result.getNode(), result.getAttributeName());
                } else {
                    if (result.getNode() == currentStructure.getRootNode()) {
                        this.clear(resolver);
                        return false;
                    }
                    tx.addRemoveNodeOperation(currentStructure.getParent(result.getNode()), result.getNode());
                }
                changes = true;
            }
            return changes;
        }, selector, resolver);
        return removedElements;
    }

    @Override
    public void clearProperty(String key, NodeKeyResolver<ImmutableNode> resolver) {
        this.clearProperty(key, null, resolver);
    }

    public void clearProperty(String key, NodeSelector selector, NodeKeyResolver<ImmutableNode> resolver) {
        this.updateModel(tx -> {
            List<QueryResult<ImmutableNode>> results = resolver.resolveKey(tx.getQueryRoot(), key, tx.getCurrentData());
            return InMemoryNodeModel.initializeClearTransaction(tx, results);
        }, selector, resolver);
    }

    @Override
    public void clear(NodeKeyResolver<ImmutableNode> resolver) {
        ImmutableNode newRoot = new ImmutableNode.Builder().name(this.getRootNode().getNodeName()).create();
        this.setRootNode(newRoot);
    }

    @Override
    public ImmutableNode getInMemoryRepresentation() {
        return this.getTreeData().getRootNode();
    }

    @Override
    public void setRootNode(ImmutableNode newRoot) {
        this.structure.set(this.createTreeData(InMemoryNodeModel.initialRootNode(newRoot), this.structure.get()));
    }

    public void replaceRoot(ImmutableNode newRoot, NodeKeyResolver<ImmutableNode> resolver) {
        if (newRoot == null) {
            throw new IllegalArgumentException("Replaced root node must not be null!");
        }
        TreeData current = this.structure.get();
        TreeData temp = this.createTreeDataForRootAndTracker(newRoot, current.getNodeTracker());
        this.structure.set(temp.updateNodeTracker(temp.getNodeTracker().update(newRoot, null, resolver, temp)));
    }

    public void mergeRoot(ImmutableNode node, String rootName, Map<ImmutableNode, ?> references, Object rootRef, NodeKeyResolver<ImmutableNode> resolver) {
        this.updateModel(tx -> {
            TreeData current = tx.getCurrentData();
            String newRootName = InMemoryNodeModel.determineRootName(current.getRootNode(), node, rootName);
            if (newRootName != null) {
                tx.addChangeNodeNameOperation(current.getRootNode(), newRootName);
            }
            tx.addAddNodesOperation(current.getRootNode(), node.getChildren());
            tx.addAttributesOperation(current.getRootNode(), node.getAttributes());
            if (node.getValue() != null) {
                tx.addChangeNodeValueOperation(current.getRootNode(), node.getValue());
            }
            if (references != null) {
                tx.addNewReferences(references);
            }
            if (rootRef != null) {
                tx.addNewReference(current.getRootNode(), rootRef);
            }
            return true;
        }, null, resolver);
    }

    public void trackNode(NodeSelector selector, NodeKeyResolver<ImmutableNode> resolver) {
        NodeTracker newTracker;
        TreeData current;
        boolean done;
        while (!(done = this.structure.compareAndSet(current = this.structure.get(), current.updateNodeTracker(newTracker = current.getNodeTracker().trackNode(current.getRootNode(), selector, resolver, current))))) {
        }
    }

    public Collection<NodeSelector> selectAndTrackNodes(String key, NodeKeyResolver<ImmutableNode> resolver) {
        List<ImmutableNode> nodes;
        TreeData current;
        boolean done;
        MutableObject refSelectors = new MutableObject();
        do {
            if (!(nodes = resolver.resolveNodeKey((current = this.structure.get()).getRootNode(), key, current)).isEmpty()) continue;
            return Collections.emptyList();
        } while (!(done = this.structure.compareAndSet(current, InMemoryNodeModel.createSelectorsForTrackedNodes((Mutable<Collection<NodeSelector>>)refSelectors, nodes, current, resolver))));
        return (Collection)refSelectors.getValue();
    }

    public Collection<NodeSelector> trackChildNodes(String key, NodeKeyResolver<ImmutableNode> resolver) {
        ImmutableNode node;
        TreeData current;
        List<ImmutableNode> nodes;
        boolean done;
        MutableObject refSelectors = new MutableObject();
        do {
            refSelectors.setValue(Collections.emptyList());
        } while (!(done = (nodes = resolver.resolveNodeKey((current = this.structure.get()).getRootNode(), key, current)).size() == 1 ? (node = nodes.get(0)).getChildren().isEmpty() || this.structure.compareAndSet(current, InMemoryNodeModel.createSelectorsForTrackedNodes((Mutable<Collection<NodeSelector>>)refSelectors, node.getChildren(), current, resolver)) : true));
        return (Collection)refSelectors.getValue();
    }

    public NodeSelector trackChildNodeWithCreation(String key, String childName, NodeKeyResolver<ImmutableNode> resolver) {
        List<ImmutableNode> nodes;
        ImmutableNode parent;
        TreeData newData;
        TreeData current;
        boolean done;
        MutableObject refSelector = new MutableObject();
        do {
            if ((nodes = resolver.resolveNodeKey((current = this.structure.get()).getRootNode(), key, current)).size() == 1) continue;
            throw new ConfigurationRuntimeException("Key does not select a single node: " + key);
        } while (!(done = this.structure.compareAndSet(current, newData = InMemoryNodeModel.createDataWithTrackedChildNode(current, parent = nodes.get(0), childName, resolver, (MutableObject<NodeSelector>)refSelector))));
        return (NodeSelector)refSelector.getValue();
    }

    public ImmutableNode getTrackedNode(NodeSelector selector) {
        return this.structure.get().getNodeTracker().getTrackedNode(selector);
    }

    public void replaceTrackedNode(NodeSelector selector, ImmutableNode newNode) {
        TreeData currentData;
        boolean done;
        if (newNode == null) {
            throw new IllegalArgumentException("Replacement node must not be null!");
        }
        while (!(done = this.replaceDetachedTrackedNode(currentData = this.structure.get(), selector, newNode) || this.replaceActiveTrackedNode(currentData, selector, newNode))) {
        }
    }

    public NodeHandler<ImmutableNode> getTrackedNodeHandler(NodeSelector selector) {
        TreeData currentData = this.structure.get();
        InMemoryNodeModel detachedNodeModel = currentData.getNodeTracker().getDetachedNodeModel(selector);
        return detachedNodeModel != null ? detachedNodeModel.getNodeHandler() : new TrackedNodeHandler(currentData.getNodeTracker().getTrackedNode(selector), currentData);
    }

    public boolean isTrackedNodeDetached(NodeSelector selector) {
        return this.structure.get().getNodeTracker().isTrackedNodeDetached(selector);
    }

    public void untrackNode(NodeSelector selector) {
        NodeTracker newTracker;
        TreeData current;
        boolean done;
        while (!(done = this.structure.compareAndSet(current = this.structure.get(), current.updateNodeTracker(newTracker = current.getNodeTracker().untrackNode(selector))))) {
        }
    }

    public ReferenceNodeHandler getReferenceNodeHandler() {
        return this.getTreeData();
    }

    TreeData getTreeData() {
        return this.structure.get();
    }

    static void updateParentMapping(final Map<ImmutableNode, ImmutableNode> parents, ImmutableNode root) {
        NodeTreeWalker.INSTANCE.walkBFS(root, new ConfigurationNodeVisitorAdapter<ImmutableNode>(){

            @Override
            public void visitBeforeChildren(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
                node.forEach(c -> parents.put(c, node));
            }
        }, DUMMY_HANDLER);
    }

    static boolean checkIfNodeDefined(ImmutableNode node) {
        return node.getValue() != null || !node.getChildren().isEmpty() || !node.getAttributes().isEmpty();
    }

    private void initializeAddTransaction(ModelTransaction tx, String key, Iterable<?> values, NodeKeyResolver<ImmutableNode> resolver) {
        NodeAddData<ImmutableNode> addData = resolver.resolveAddKey(tx.getQueryRoot(), key, tx.getCurrentData());
        if (addData.isAttribute()) {
            InMemoryNodeModel.addAttributeProperty(tx, addData, values);
        } else {
            InMemoryNodeModel.addNodeProperty(tx, addData, values);
        }
    }

    private TreeData createTreeData(ImmutableNode root, TreeData current) {
        NodeTracker newTracker = current != null ? current.getNodeTracker().detachAllTrackedNodes() : new NodeTracker();
        return this.createTreeDataForRootAndTracker(root, newTracker);
    }

    private TreeData createTreeDataForRootAndTracker(ImmutableNode root, NodeTracker newTracker) {
        return new TreeData(root, this.createParentMapping(root), Collections.emptyMap(), newTracker, new ReferenceTracker());
    }

    private static void addNodeProperty(ModelTransaction tx, NodeAddData<ImmutableNode> addData, Iterable<?> values) {
        Collection<ImmutableNode> newNodes = InMemoryNodeModel.createNodesToAdd(addData.getNewNodeName(), values);
        InMemoryNodeModel.addNodesByAddData(tx, addData, newNodes);
    }

    private static void addNodesByAddData(ModelTransaction tx, NodeAddData<ImmutableNode> addData, Collection<ImmutableNode> newNodes) {
        if (addData.getPathNodes().isEmpty()) {
            tx.addAddNodesOperation(addData.getParent(), newNodes);
        } else {
            ImmutableNode newChild = InMemoryNodeModel.createNodeToAddWithPath(addData, newNodes);
            tx.addAddNodeOperation(addData.getParent(), newChild);
        }
    }

    private static void addAttributeProperty(ModelTransaction tx, NodeAddData<ImmutableNode> addData, Iterable<?> values) {
        if (addData.getPathNodes().isEmpty()) {
            tx.addAttributeOperation(addData.getParent(), addData.getNewNodeName(), values.iterator().next());
        } else {
            int pathNodeCount = addData.getPathNodes().size();
            ImmutableNode childWithAttribute = new ImmutableNode.Builder().name(addData.getPathNodes().get(pathNodeCount - 1)).addAttribute(addData.getNewNodeName(), values.iterator().next()).create();
            ImmutableNode newChild = pathNodeCount > 1 ? InMemoryNodeModel.createNodeOnPath(addData.getPathNodes().subList(0, pathNodeCount - 1).iterator(), Collections.singleton(childWithAttribute)) : childWithAttribute;
            tx.addAddNodeOperation(addData.getParent(), newChild);
        }
    }

    private static Collection<ImmutableNode> createNodesToAdd(String newNodeName, Iterable<?> values) {
        LinkedList<ImmutableNode> nodes = new LinkedList<ImmutableNode>();
        values.forEach(value -> nodes.add(new ImmutableNode.Builder().name(newNodeName).value(value).create()));
        return nodes;
    }

    private static ImmutableNode createNodeToAddWithPath(NodeAddData<ImmutableNode> addData, Collection<ImmutableNode> newNodes) {
        return InMemoryNodeModel.createNodeOnPath(addData.getPathNodes().iterator(), newNodes);
    }

    private static ImmutableNode createNodeOnPath(Iterator<String> it, Collection<ImmutableNode> newNodes) {
        ImmutableNode.Builder builder;
        String nodeName = it.next();
        if (it.hasNext()) {
            builder = new ImmutableNode.Builder(1);
            builder.addChild(InMemoryNodeModel.createNodeOnPath(it, newNodes));
        } else {
            builder = new ImmutableNode.Builder(newNodes.size());
            builder.addChildren(newNodes);
        }
        return builder.name(nodeName).create();
    }

    private static boolean initializeClearTransaction(ModelTransaction tx, Collection<QueryResult<ImmutableNode>> results) {
        results.forEach(result -> {
            if (result.isAttributeResult()) {
                tx.addRemoveAttributeOperation((ImmutableNode)result.getNode(), result.getAttributeName());
            } else {
                tx.addClearNodeValueOperation((ImmutableNode)result.getNode());
            }
        });
        return !results.isEmpty();
    }

    private static boolean initializeUpdateTransaction(ModelTransaction tx, Map<QueryResult<ImmutableNode>, Object> changedValues) {
        changedValues.forEach((k, v) -> {
            ImmutableNode node = (ImmutableNode)k.getNode();
            if (k.isAttributeResult()) {
                tx.addAttributeOperation(node, k.getAttributeName(), v);
            } else {
                tx.addChangeNodeValueOperation(node, v);
            }
        });
        return !changedValues.isEmpty();
    }

    private static ImmutableNode initialRootNode(ImmutableNode providedRoot) {
        return providedRoot != null ? providedRoot : new ImmutableNode.Builder().create();
    }

    private static String determineRootName(ImmutableNode rootNode, ImmutableNode node, String rootName) {
        if (rootName != null) {
            return rootName;
        }
        if (rootNode.getNodeName() == null) {
            return node.getNodeName();
        }
        return null;
    }

    private Map<ImmutableNode, ImmutableNode> createParentMapping(ImmutableNode root) {
        HashMap<ImmutableNode, ImmutableNode> parents = new HashMap<ImmutableNode, ImmutableNode>();
        InMemoryNodeModel.updateParentMapping(parents, root);
        return parents;
    }

    private void updateModel(TransactionInitializer txInit, NodeSelector selector, NodeKeyResolver<ImmutableNode> resolver) {
        TreeData currentData;
        boolean done;
        while (!(done = this.executeTransactionOnDetachedTrackedNode(txInit, selector, currentData = this.getTreeData(), resolver) || this.executeTransactionOnCurrentStructure(txInit, selector, currentData, resolver))) {
        }
    }

    private boolean executeTransactionOnCurrentStructure(TransactionInitializer txInit, NodeSelector selector, TreeData currentData, NodeKeyResolver<ImmutableNode> resolver) {
        boolean done;
        ModelTransaction tx = new ModelTransaction(currentData, selector, resolver);
        if (!txInit.initTransaction(tx)) {
            done = true;
        } else {
            TreeData newData = tx.execute();
            done = this.structure.compareAndSet(tx.getCurrentData(), newData);
        }
        return done;
    }

    private boolean executeTransactionOnDetachedTrackedNode(TransactionInitializer txInit, NodeSelector selector, TreeData currentData, NodeKeyResolver<ImmutableNode> resolver) {
        InMemoryNodeModel detachedNodeModel;
        if (selector != null && (detachedNodeModel = currentData.getNodeTracker().getDetachedNodeModel(selector)) != null) {
            detachedNodeModel.updateModel(txInit, null, resolver);
            return true;
        }
        return false;
    }

    private boolean replaceDetachedTrackedNode(TreeData currentData, NodeSelector selector, ImmutableNode newNode) {
        InMemoryNodeModel detachedNodeModel = currentData.getNodeTracker().getDetachedNodeModel(selector);
        if (detachedNodeModel != null) {
            detachedNodeModel.setRootNode(newNode);
            return true;
        }
        return false;
    }

    private boolean replaceActiveTrackedNode(TreeData currentData, NodeSelector selector, ImmutableNode newNode) {
        NodeTracker newTracker = currentData.getNodeTracker().replaceAndDetachTrackedNode(selector, newNode);
        return this.structure.compareAndSet(currentData, currentData.updateNodeTracker(newTracker));
    }

    private static TreeData createSelectorsForTrackedNodes(Mutable<Collection<NodeSelector>> refSelectors, List<ImmutableNode> nodes, TreeData current, NodeKeyResolver<ImmutableNode> resolver) {
        ArrayList<NodeSelector> selectors = new ArrayList<NodeSelector>(nodes.size());
        HashMap cache = new HashMap();
        nodes.forEach(node -> selectors.add(new NodeSelector(resolver.nodeKey((ImmutableNode)node, cache, current))));
        refSelectors.setValue(selectors);
        NodeTracker newTracker = current.getNodeTracker().trackNodes(selectors, nodes);
        return current.updateNodeTracker(newTracker);
    }

    private static TreeData updateDataWithNewTrackedNode(TreeData current, ImmutableNode node, NodeKeyResolver<ImmutableNode> resolver, MutableObject<NodeSelector> refSelector) {
        NodeSelector selector = new NodeSelector(resolver.nodeKey(node, new HashMap(), current));
        refSelector.setValue((Object)selector);
        NodeTracker newTracker = current.getNodeTracker().trackNodes(Collections.singleton(selector), Collections.singleton(node));
        return current.updateNodeTracker(newTracker);
    }

    private static TreeData createDataWithTrackedChildNode(TreeData current, ImmutableNode parent, String childName, NodeKeyResolver<ImmutableNode> resolver, MutableObject<NodeSelector> refSelector) {
        TreeData newData;
        List<ImmutableNode> namedChildren = current.getChildren(parent, childName);
        if (!namedChildren.isEmpty()) {
            newData = InMemoryNodeModel.updateDataWithNewTrackedNode(current, namedChildren.get(0), resolver, refSelector);
        } else {
            ImmutableNode child = new ImmutableNode.Builder().name(childName).create();
            ModelTransaction tx = new ModelTransaction(current, null, resolver);
            tx.addAddNodeOperation(parent, child);
            newData = InMemoryNodeModel.updateDataWithNewTrackedNode(tx.execute(), child, resolver, refSelector);
        }
        return newData;
    }

    private static boolean valuesNotEmpty(Iterable<?> values) {
        return values.iterator().hasNext();
    }

    private static IllegalArgumentException attributeKeyException(String key) {
        return new IllegalArgumentException("New nodes cannot be added to an attribute key: " + key);
    }

    private static interface TransactionInitializer {
        public boolean initTransaction(ModelTransaction var1);
    }
}

