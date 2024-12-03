/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.NodeSelector;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.ReferenceTracker;
import org.apache.commons.configuration2.tree.TreeData;

class ModelTransaction {
    private static final int MAX_REPLACEMENTS = 200;
    private static final int LEVEL_UNKNOWN = -1;
    private final TreeData currentData;
    private final ImmutableNode queryRoot;
    private final NodeSelector rootNodeSelector;
    private final NodeKeyResolver<ImmutableNode> resolver;
    private final Map<ImmutableNode, ImmutableNode> replacementMapping;
    private final Map<ImmutableNode, ImmutableNode> replacedNodes;
    private final Map<ImmutableNode, ImmutableNode> parentMapping;
    private final Collection<ImmutableNode> addedNodes;
    private final Collection<ImmutableNode> removedNodes;
    private final Collection<ImmutableNode> allRemovedNodes;
    private final SortedMap<Integer, Map<ImmutableNode, Operations>> operations;
    private Map<ImmutableNode, Object> newReferences;
    private ImmutableNode newRoot;

    public ModelTransaction(TreeData treeData, NodeSelector selector, NodeKeyResolver<ImmutableNode> resolver) {
        this.currentData = treeData;
        this.resolver = resolver;
        this.replacementMapping = this.getCurrentData().copyReplacementMapping();
        this.replacedNodes = new HashMap<ImmutableNode, ImmutableNode>();
        this.parentMapping = this.getCurrentData().copyParentMapping();
        this.operations = new TreeMap<Integer, Map<ImmutableNode, Operations>>();
        this.addedNodes = new LinkedList<ImmutableNode>();
        this.removedNodes = new LinkedList<ImmutableNode>();
        this.allRemovedNodes = new LinkedList<ImmutableNode>();
        this.queryRoot = this.initQueryRoot(treeData, selector);
        this.rootNodeSelector = selector;
    }

    public NodeKeyResolver<ImmutableNode> getResolver() {
        return this.resolver;
    }

    public ImmutableNode getQueryRoot() {
        return this.queryRoot;
    }

    public void addAddNodesOperation(ImmutableNode parent, Collection<? extends ImmutableNode> newNodes) {
        ChildrenUpdateOperation op = new ChildrenUpdateOperation();
        op.addNewNodes(newNodes);
        this.fetchOperations(parent, -1).addChildrenOperation(op);
    }

    public void addAddNodeOperation(ImmutableNode parent, ImmutableNode newChild) {
        ChildrenUpdateOperation op = new ChildrenUpdateOperation();
        op.addNewNode(newChild);
        this.fetchOperations(parent, -1).addChildrenOperation(op);
    }

    public void addAttributeOperation(ImmutableNode target, String name, Object value) {
        this.fetchOperations(target, -1).addOperation(new AddAttributeOperation(name, value));
    }

    public void addAttributesOperation(ImmutableNode target, Map<String, Object> attributes) {
        this.fetchOperations(target, -1).addOperation(new AddAttributesOperation(attributes));
    }

    public void addRemoveNodeOperation(ImmutableNode parent, ImmutableNode node) {
        ChildrenUpdateOperation op = new ChildrenUpdateOperation();
        op.addNodeToRemove(node);
        this.fetchOperations(parent, -1).addChildrenOperation(op);
    }

    public void addRemoveAttributeOperation(ImmutableNode target, String name) {
        this.fetchOperations(target, -1).addOperation(new RemoveAttributeOperation(name));
    }

    public void addClearNodeValueOperation(ImmutableNode target) {
        this.addChangeNodeValueOperation(target, null);
    }

    public void addChangeNodeValueOperation(ImmutableNode target, Object newValue) {
        this.fetchOperations(target, -1).addOperation(new ChangeNodeValueOperation(newValue));
    }

    public void addChangeNodeNameOperation(ImmutableNode target, String newName) {
        this.fetchOperations(target, -1).addOperation(new ChangeNodeNameOperation(newName));
    }

    public void addNewReferences(Map<ImmutableNode, ?> refs) {
        this.fetchReferenceMap().putAll(refs);
    }

    public void addNewReference(ImmutableNode node, Object ref) {
        this.fetchReferenceMap().put(node, ref);
    }

    public TreeData execute() {
        this.executeOperations();
        this.updateParentMapping();
        return new TreeData(this.newRoot, this.parentMapping, this.replacementMapping, this.currentData.getNodeTracker().update(this.newRoot, this.rootNodeSelector, this.getResolver(), this.getCurrentData()), this.updateReferenceTracker());
    }

    public TreeData getCurrentData() {
        return this.currentData;
    }

    ImmutableNode getParent(ImmutableNode node) {
        return this.getCurrentData().getParent(node);
    }

    Operations fetchOperations(ImmutableNode target, int level) {
        Integer nodeLevel = level == -1 ? this.level(target) : level;
        Map levelOperations = this.operations.computeIfAbsent(nodeLevel, k -> new HashMap());
        Operations ops = (Operations)levelOperations.get(target);
        if (ops == null) {
            ops = new Operations();
            levelOperations.put(target, ops);
        }
        return ops;
    }

    private ImmutableNode initQueryRoot(TreeData treeData, NodeSelector selector) {
        return selector == null ? treeData.getRootNode() : treeData.getNodeTracker().getTrackedNode(selector);
    }

    private int level(ImmutableNode node) {
        ImmutableNode current = this.getCurrentData().getParent(node);
        int level = 0;
        while (current != null) {
            ++level;
            current = this.getCurrentData().getParent(current);
        }
        return level;
    }

    private void executeOperations() {
        while (!this.operations.isEmpty()) {
            Integer level = this.operations.lastKey();
            ((Map)this.operations.remove(level)).forEach((k, v) -> v.apply((ImmutableNode)k, level));
        }
    }

    private void updateParentMapping() {
        this.replacementMapping.putAll(this.replacedNodes);
        if (this.replacementMapping.size() > 200) {
            this.rebuildParentMapping();
        } else {
            this.updateParentMappingForAddedNodes();
            this.updateParentMappingForRemovedNodes();
        }
    }

    private void rebuildParentMapping() {
        this.replacementMapping.clear();
        this.parentMapping.clear();
        InMemoryNodeModel.updateParentMapping(this.parentMapping, this.newRoot);
    }

    private void updateParentMappingForAddedNodes() {
        this.addedNodes.forEach(node -> InMemoryNodeModel.updateParentMapping(this.parentMapping, node));
    }

    private void updateParentMappingForRemovedNodes() {
        this.removedNodes.forEach(this::removeNodesFromParentAndReplacementMapping);
    }

    private void removeNodesFromParentAndReplacementMapping(ImmutableNode root) {
        NodeTreeWalker.INSTANCE.walkBFS(root, new ConfigurationNodeVisitorAdapter<ImmutableNode>(){

            @Override
            public void visitBeforeChildren(ImmutableNode node, NodeHandler<ImmutableNode> handler) {
                ModelTransaction.this.allRemovedNodes.add(node);
                ModelTransaction.this.parentMapping.remove(node);
                ModelTransaction.this.removeNodeFromReplacementMapping(node);
            }
        }, this.getCurrentData());
    }

    private void removeNodeFromReplacementMapping(ImmutableNode node) {
        ImmutableNode replacement = node;
        while ((replacement = this.replacementMapping.remove(replacement)) != null) {
        }
    }

    private ReferenceTracker updateReferenceTracker() {
        ReferenceTracker tracker = this.currentData.getReferenceTracker();
        if (this.newReferences != null) {
            tracker = tracker.addReferences(this.newReferences);
        }
        return tracker.updateReferences(this.replacedNodes, this.allRemovedNodes);
    }

    private Map<ImmutableNode, Object> fetchReferenceMap() {
        if (this.newReferences == null) {
            this.newReferences = new HashMap<ImmutableNode, Object>();
        }
        return this.newReferences;
    }

    private static <E> Collection<E> concatenate(Collection<E> col1, Collection<? extends E> col2) {
        if (col2 == null) {
            return col1;
        }
        ArrayList<E> result = col1 != null ? col1 : new ArrayList<E>(col2.size());
        result.addAll(col2);
        return result;
    }

    private static <E> Set<E> concatenate(Set<E> set1, Set<? extends E> set2) {
        if (set2 == null) {
            return set1;
        }
        HashSet<? extends E> result = set1 != null ? set1 : new HashSet<E>();
        result.addAll(set2);
        return result;
    }

    private static <K, V> Map<K, V> concatenate(Map<K, V> map1, Map<? extends K, ? extends V> map2) {
        if (map2 == null) {
            return map1;
        }
        HashMap<? extends K, ? extends V> result = map1 != null ? map1 : new HashMap<K, V>();
        result.putAll(map2);
        return result;
    }

    private static <E> Collection<E> append(Collection<E> col, E node) {
        LinkedList<E> result = col != null ? col : new LinkedList<E>();
        result.add(node);
        return result;
    }

    private static <E> Set<E> append(Set<E> col, E elem) {
        HashSet<E> result = col != null ? col : new HashSet<E>();
        result.add(elem);
        return result;
    }

    private static <K, V> Map<K, V> append(Map<K, V> map, K key, V value) {
        HashMap<K, V> result = map != null ? map : new HashMap<K, V>();
        result.put(key, value);
        return result;
    }

    private class Operations {
        private ChildrenUpdateOperation childrenOperation;
        private Collection<Operation> operations;
        private Collection<ImmutableNode> addedNodesInOperation;

        private Operations() {
        }

        public void addChildrenOperation(ChildrenUpdateOperation co) {
            if (this.childrenOperation == null) {
                this.childrenOperation = co;
            } else {
                this.childrenOperation.combine(co);
            }
        }

        public void addOperation(Operation op) {
            this.operations = ModelTransaction.append(this.operations, op);
        }

        public void newNodesAdded(Collection<ImmutableNode> newNodes) {
            this.addedNodesInOperation = ModelTransaction.concatenate(this.addedNodesInOperation, newNodes);
        }

        public void apply(ImmutableNode target, int level) {
            ImmutableNode node = target;
            if (this.childrenOperation != null) {
                node = this.childrenOperation.apply(node, this);
            }
            if (this.operations != null) {
                for (Operation op : this.operations) {
                    node = op.apply(node, this);
                }
            }
            this.handleAddedNodes(node);
            if (level == 0) {
                ModelTransaction.this.newRoot = node;
                ModelTransaction.this.replacedNodes.put(target, node);
            } else {
                this.propagateChange(target, node, level);
            }
        }

        private void propagateChange(ImmutableNode target, ImmutableNode node, int level) {
            ImmutableNode parent = ModelTransaction.this.getParent(target);
            ChildrenUpdateOperation co = new ChildrenUpdateOperation();
            if (InMemoryNodeModel.checkIfNodeDefined(node)) {
                co.addNodeToReplace(target, node);
            } else {
                co.addNodeToRemove(target);
            }
            ModelTransaction.this.fetchOperations(parent, level - 1).addChildrenOperation(co);
        }

        private void handleAddedNodes(ImmutableNode node) {
            if (this.addedNodesInOperation != null) {
                this.addedNodesInOperation.forEach(child -> {
                    ModelTransaction.this.parentMapping.put(child, node);
                    ModelTransaction.this.addedNodes.add(child);
                });
            }
        }
    }

    private static class ChangeNodeNameOperation
    extends Operation {
        private final String newName;

        public ChangeNodeNameOperation(String name) {
            this.newName = name;
        }

        @Override
        protected ImmutableNode apply(ImmutableNode target, Operations operations) {
            return target.setName(this.newName);
        }
    }

    private static class ChangeNodeValueOperation
    extends Operation {
        private final Object newValue;

        public ChangeNodeValueOperation(Object value) {
            this.newValue = value;
        }

        @Override
        protected ImmutableNode apply(ImmutableNode target, Operations operations) {
            return target.setValue(this.newValue);
        }
    }

    private static class RemoveAttributeOperation
    extends Operation {
        private final String attributeName;

        public RemoveAttributeOperation(String name) {
            this.attributeName = name;
        }

        @Override
        protected ImmutableNode apply(ImmutableNode target, Operations operations) {
            return target.removeAttribute(this.attributeName);
        }
    }

    private static class AddAttributesOperation
    extends Operation {
        private final Map<String, Object> attributes;

        public AddAttributesOperation(Map<String, Object> attrs) {
            this.attributes = attrs;
        }

        @Override
        protected ImmutableNode apply(ImmutableNode target, Operations operations) {
            return target.setAttributes(this.attributes);
        }
    }

    private static class AddAttributeOperation
    extends Operation {
        private final String attributeName;
        private final Object attributeValue;

        public AddAttributeOperation(String name, Object value) {
            this.attributeName = name;
            this.attributeValue = value;
        }

        @Override
        protected ImmutableNode apply(ImmutableNode target, Operations operations) {
            return target.setAttribute(this.attributeName, this.attributeValue);
        }
    }

    private class ChildrenUpdateOperation
    extends Operation {
        private Collection<ImmutableNode> newNodes;
        private Set<ImmutableNode> nodesToRemove;
        private Map<ImmutableNode, ImmutableNode> nodesToReplace;

        private ChildrenUpdateOperation() {
        }

        public void combine(ChildrenUpdateOperation op) {
            this.newNodes = ModelTransaction.concatenate(this.newNodes, op.newNodes);
            this.nodesToReplace = ModelTransaction.concatenate(this.nodesToReplace, op.nodesToReplace);
            this.nodesToRemove = ModelTransaction.concatenate(this.nodesToRemove, op.nodesToRemove);
        }

        public void addNewNode(ImmutableNode node) {
            this.newNodes = ModelTransaction.append(this.newNodes, node);
        }

        public void addNewNodes(Collection<? extends ImmutableNode> nodes) {
            this.newNodes = ModelTransaction.concatenate(this.newNodes, nodes);
        }

        public void addNodeToReplace(ImmutableNode org, ImmutableNode replacement) {
            this.nodesToReplace = ModelTransaction.append(this.nodesToReplace, org, replacement);
        }

        public void addNodeToRemove(ImmutableNode node) {
            this.nodesToRemove = ModelTransaction.append(this.nodesToRemove, node);
        }

        @Override
        protected ImmutableNode apply(ImmutableNode target, Operations operations) {
            Map<ImmutableNode, ImmutableNode> replacements = this.fetchReplacementMap();
            Set<ImmutableNode> removals = this.fetchRemovalSet();
            LinkedList<ImmutableNode> resultNodes = new LinkedList<ImmutableNode>();
            for (ImmutableNode nd : target) {
                ImmutableNode repl = replacements.get(nd);
                if (repl != null) {
                    resultNodes.add(repl);
                    ModelTransaction.this.replacedNodes.put(nd, repl);
                    continue;
                }
                if (removals.contains(nd)) {
                    ModelTransaction.this.removedNodes.add(nd);
                    continue;
                }
                resultNodes.add(nd);
            }
            ModelTransaction.concatenate(resultNodes, this.newNodes);
            operations.newNodesAdded(this.newNodes);
            return target.replaceChildren(resultNodes);
        }

        private Map<ImmutableNode, ImmutableNode> fetchReplacementMap() {
            return this.nodesToReplace != null ? this.nodesToReplace : Collections.emptyMap();
        }

        private Set<ImmutableNode> fetchRemovalSet() {
            return this.nodesToRemove != null ? this.nodesToRemove : Collections.emptySet();
        }
    }

    private static abstract class Operation {
        private Operation() {
        }

        protected abstract ImmutableNode apply(ImmutableNode var1, Operations var2);
    }
}

