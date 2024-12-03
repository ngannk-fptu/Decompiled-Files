/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.sync.NoOpSynchronizer;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.DefaultExpressionEngine;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.NodeAddData;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.NodeModel;
import org.apache.commons.configuration2.tree.NodeTreeWalker;
import org.apache.commons.configuration2.tree.NodeUpdateData;
import org.apache.commons.configuration2.tree.QueryResult;

public abstract class AbstractHierarchicalConfiguration<T>
extends AbstractConfiguration
implements Cloneable,
NodeKeyResolver<T>,
HierarchicalConfiguration<T> {
    private NodeModel<T> model;
    private ExpressionEngine expressionEngine;

    protected AbstractHierarchicalConfiguration(NodeModel<T> nodeModel) {
        this.model = nodeModel;
    }

    @Override
    public final String getRootElementName() {
        this.beginRead(false);
        try {
            String string = this.getRootElementNameInternal();
            return string;
        }
        finally {
            this.endRead();
        }
    }

    protected String getRootElementNameInternal() {
        NodeHandler<T> nodeHandler = this.getModel().getNodeHandler();
        return nodeHandler.nodeName(nodeHandler.getRootNode());
    }

    @Override
    public NodeModel<T> getNodeModel() {
        this.beginRead(false);
        try {
            NodeModel<T> nodeModel = this.getModel();
            return nodeModel;
        }
        finally {
            this.endRead();
        }
    }

    @Override
    public ExpressionEngine getExpressionEngine() {
        return this.expressionEngine != null ? this.expressionEngine : DefaultExpressionEngine.INSTANCE;
    }

    @Override
    public void setExpressionEngine(ExpressionEngine expressionEngine) {
        this.expressionEngine = expressionEngine;
    }

    @Override
    protected Object getPropertyInternal(String key) {
        List<QueryResult<T>> results = this.fetchNodeList(key);
        if (results.isEmpty()) {
            return null;
        }
        NodeHandler<T> handler = this.getModel().getNodeHandler();
        List list = results.stream().map(r -> this.valueFromResult((QueryResult<T>)r, handler)).filter(Objects::nonNull).collect(Collectors.toList());
        if (list.size() < 1) {
            return null;
        }
        return list.size() == 1 ? list.get(0) : list;
    }

    @Override
    protected void addPropertyInternal(String key, Object obj) {
        this.addPropertyToModel(key, this.getListDelimiterHandler().parse(obj));
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {
        this.addPropertyToModel(key, Collections.singleton(value));
    }

    private void addPropertyToModel(String key, Iterable<?> values) {
        this.getModel().addProperty(key, values, this);
    }

    @Override
    public final void addNodes(String key, Collection<? extends T> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.ADD_NODES, key, nodes, true);
            this.addNodesInternal(key, nodes);
            this.fireEvent(ConfigurationEvent.ADD_NODES, key, nodes, false);
        }
        finally {
            this.endWrite();
        }
    }

    protected void addNodesInternal(String key, Collection<? extends T> nodes) {
        this.getModel().addNodes(key, nodes, this);
    }

    @Override
    protected boolean isEmptyInternal() {
        return !this.nodeDefined(this.getModel().getNodeHandler().getRootNode());
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.getPropertyInternal(key) != null;
    }

    @Override
    protected void setPropertyInternal(String key, Object value) {
        this.getModel().setProperty(key, value, this);
    }

    @Override
    public List<QueryResult<T>> resolveKey(T root, String key, NodeHandler<T> handler) {
        return this.getExpressionEngine().query(root, key, handler);
    }

    @Override
    public List<T> resolveNodeKey(T root, String key, NodeHandler<T> handler) {
        return this.resolveKey(root, key, handler).stream().filter(r -> !r.isAttributeResult()).map(QueryResult::getNode).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public NodeAddData<T> resolveAddKey(T root, String key, NodeHandler<T> handler) {
        return this.getExpressionEngine().prepareAdd(root, key, handler);
    }

    @Override
    public NodeUpdateData<T> resolveUpdateKey(T root, String key, Object newValue, NodeHandler<T> handler) {
        Iterator<QueryResult<T>> itNodes = this.fetchNodeList(key).iterator();
        Iterator<?> itValues = this.getListDelimiterHandler().parse(newValue).iterator();
        HashMap changedValues = new HashMap();
        LinkedList<Object> additionalValues = null;
        LinkedList removedItems = null;
        while (itNodes.hasNext() && itValues.hasNext()) {
            changedValues.put(itNodes.next(), itValues.next());
        }
        if (itValues.hasNext()) {
            additionalValues = new LinkedList<Object>();
            itValues.forEachRemaining(additionalValues::add);
        }
        if (itNodes.hasNext()) {
            removedItems = new LinkedList();
            itNodes.forEachRemaining(removedItems::add);
        }
        return new NodeUpdateData(changedValues, additionalValues, removedItems, key);
    }

    @Override
    public String nodeKey(T node, Map<T, String> cache, NodeHandler<T> handler) {
        LinkedList<T> paths = new LinkedList<T>();
        T currentNode = node;
        String key = cache.get(node);
        while (key == null && currentNode != null) {
            paths.add(0, currentNode);
            currentNode = handler.getParent(currentNode);
            key = cache.get(currentNode);
        }
        for (Object n : paths) {
            String currentKey = this.getExpressionEngine().canonicalKey(n, key, handler);
            cache.put(n, currentKey);
            key = currentKey;
        }
        return key;
    }

    @Override
    protected void clearInternal() {
        this.getModel().clear(this);
    }

    @Override
    public final void clearTree(String key) {
        this.beginWrite(false);
        try {
            this.fireEvent(ConfigurationEvent.CLEAR_TREE, key, null, true);
            this.fireEvent(ConfigurationEvent.CLEAR_TREE, key, this.clearTreeInternal(key), false);
        }
        finally {
            this.endWrite();
        }
    }

    protected Object clearTreeInternal(String key) {
        return this.getModel().clearTree(key, this);
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.getModel().clearProperty(key, this);
    }

    @Override
    protected int sizeInternal() {
        return this.visitDefinedKeys().getKeyList().size();
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return this.visitDefinedKeys().getKeyList().iterator();
    }

    private DefinedKeysVisitor visitDefinedKeys() {
        DefinedKeysVisitor visitor = new DefinedKeysVisitor();
        NodeHandler<T> nodeHandler = this.getModel().getNodeHandler();
        NodeTreeWalker.INSTANCE.walkDFS(nodeHandler.getRootNode(), visitor, nodeHandler);
        return visitor;
    }

    @Override
    protected Iterator<String> getKeysInternal(String prefix) {
        DefinedKeysVisitor visitor = new DefinedKeysVisitor(prefix);
        if (this.containsKey(prefix)) {
            visitor.getKeyList().add(prefix);
        }
        List<QueryResult<QueryResult>> results = this.fetchNodeList(prefix);
        NodeHandler handler = this.getModel().getNodeHandler();
        results.forEach(result -> {
            if (!result.isAttributeResult()) {
                handler.getChildren(result.getNode()).forEach(c -> NodeTreeWalker.INSTANCE.walkDFS(c, visitor, handler));
                visitor.handleAttributeKeys(prefix, result.getNode(), handler);
            }
        });
        return visitor.getKeyList().iterator();
    }

    @Override
    public final int getMaxIndex(String key) {
        this.beginRead(false);
        try {
            int n = this.getMaxIndexInternal(key);
            return n;
        }
        finally {
            this.endRead();
        }
    }

    protected int getMaxIndexInternal(String key) {
        return this.fetchNodeList(key).size() - 1;
    }

    @Override
    public Object clone() {
        this.beginRead(false);
        try {
            AbstractHierarchicalConfiguration copy = (AbstractHierarchicalConfiguration)super.clone();
            copy.setSynchronizer(NoOpSynchronizer.INSTANCE);
            copy.cloneInterpolator(this);
            copy.setSynchronizer(ConfigurationUtils.cloneSynchronizer(this.getSynchronizer()));
            copy.model = this.cloneNodeModel();
            AbstractHierarchicalConfiguration abstractHierarchicalConfiguration = copy;
            return abstractHierarchicalConfiguration;
        }
        catch (CloneNotSupportedException cex) {
            throw new ConfigurationRuntimeException(cex);
        }
        finally {
            this.endRead();
        }
    }

    protected abstract NodeModel<T> cloneNodeModel();

    protected List<QueryResult<T>> fetchNodeList(String key) {
        NodeHandler<T> nodeHandler = this.getModel().getNodeHandler();
        return this.resolveKey(nodeHandler.getRootNode(), key, nodeHandler);
    }

    protected boolean nodeDefined(T node) {
        DefinedVisitor visitor = new DefinedVisitor();
        NodeTreeWalker.INSTANCE.walkBFS(node, visitor, this.getModel().getNodeHandler());
        return visitor.isDefined();
    }

    protected NodeModel<T> getModel() {
        return this.model;
    }

    private Object valueFromResult(QueryResult<T> result, NodeHandler<T> handler) {
        return result.isAttributeResult() ? result.getAttributeValue(handler) : handler.getValue(result.getNode());
    }

    public String toString() {
        return super.toString() + "(" + this.getRootElementNameInternal() + ")";
    }

    private class DefinedKeysVisitor
    extends ConfigurationNodeVisitorAdapter<T> {
        private final Set<String> keyList = new LinkedHashSet<String>();
        private final Stack<String> parentKeys = new Stack();

        public DefinedKeysVisitor() {
        }

        public DefinedKeysVisitor(String prefix) {
            this();
            this.parentKeys.push(prefix);
        }

        public Set<String> getKeyList() {
            return this.keyList;
        }

        @Override
        public void visitAfterChildren(T node, NodeHandler<T> handler) {
            this.parentKeys.pop();
        }

        @Override
        public void visitBeforeChildren(T node, NodeHandler<T> handler) {
            String parentKey = this.parentKeys.isEmpty() ? null : this.parentKeys.peek();
            String key = AbstractHierarchicalConfiguration.this.getExpressionEngine().nodeKey(node, parentKey, handler);
            this.parentKeys.push(key);
            if (handler.getValue(node) != null) {
                this.keyList.add(key);
            }
            this.handleAttributeKeys(key, node, handler);
        }

        public void handleAttributeKeys(String parentKey, T node, NodeHandler<T> handler) {
            handler.getAttributes(node).forEach(attr -> this.keyList.add(AbstractHierarchicalConfiguration.this.getExpressionEngine().attributeKey(parentKey, (String)attr)));
        }
    }

    private static class DefinedVisitor<T>
    extends ConfigurationNodeVisitorAdapter<T> {
        private boolean defined;

        private DefinedVisitor() {
        }

        @Override
        public boolean terminate() {
            return this.isDefined();
        }

        @Override
        public void visitBeforeChildren(T node, NodeHandler<T> handler) {
            this.defined = handler.getValue(node) != null || !handler.getAttributes(node).isEmpty();
        }

        public boolean isDefined() {
            return this.defined;
        }
    }
}

