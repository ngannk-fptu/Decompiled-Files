/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.tree;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration2.tree.DefaultConfigurationKey;
import org.apache.commons.configuration2.tree.DefaultExpressionEngineSymbols;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.NodeAddData;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeMatcher;
import org.apache.commons.configuration2.tree.NodeNameMatchers;
import org.apache.commons.configuration2.tree.QueryResult;
import org.apache.commons.lang3.StringUtils;

public class DefaultExpressionEngine
implements ExpressionEngine {
    public static final DefaultExpressionEngine INSTANCE = new DefaultExpressionEngine(DefaultExpressionEngineSymbols.DEFAULT_SYMBOLS);
    private final DefaultExpressionEngineSymbols symbols;
    private final NodeMatcher<String> nameMatcher;

    public DefaultExpressionEngine(DefaultExpressionEngineSymbols syms) {
        this(syms, null);
    }

    public DefaultExpressionEngine(DefaultExpressionEngineSymbols syms, NodeMatcher<String> nodeNameMatcher) {
        if (syms == null) {
            throw new IllegalArgumentException("Symbols must not be null!");
        }
        this.symbols = syms;
        this.nameMatcher = nodeNameMatcher != null ? nodeNameMatcher : NodeNameMatchers.EQUALS;
    }

    public DefaultExpressionEngineSymbols getSymbols() {
        return this.symbols;
    }

    @Override
    public <T> List<QueryResult<T>> query(T root, String key, NodeHandler<T> handler) {
        LinkedList<QueryResult<T>> results = new LinkedList<QueryResult<T>>();
        this.findNodesForKey(new DefaultConfigurationKey(this, key).iterator(), root, results, handler);
        return results;
    }

    @Override
    public <T> String nodeKey(T node, String parentKey, NodeHandler<T> handler) {
        if (parentKey == null) {
            return "";
        }
        DefaultConfigurationKey key = new DefaultConfigurationKey(this, parentKey);
        key.append(handler.nodeName(node), true);
        return key.toString();
    }

    @Override
    public String attributeKey(String parentKey, String attributeName) {
        DefaultConfigurationKey key = new DefaultConfigurationKey(this, parentKey);
        key.appendAttribute(attributeName);
        return key.toString();
    }

    @Override
    public <T> String canonicalKey(T node, String parentKey, NodeHandler<T> handler) {
        String nodeName = handler.nodeName(node);
        T parent = handler.getParent(node);
        DefaultConfigurationKey key = new DefaultConfigurationKey(this, parentKey);
        key.append(StringUtils.defaultString((String)nodeName));
        if (parent != null) {
            key.appendIndex(this.determineIndex(node, parent, nodeName, handler));
        }
        return key.toString();
    }

    @Override
    public <T> NodeAddData<T> prepareAdd(T root, String key, NodeHandler<T> handler) {
        DefaultConfigurationKey.KeyIterator it = new DefaultConfigurationKey(this, key).iterator();
        if (!it.hasNext()) {
            throw new IllegalArgumentException("Key for add operation must be defined!");
        }
        T parent = this.findLastPathNode(it, root, handler);
        LinkedList<String> pathNodes = new LinkedList<String>();
        while (it.hasNext()) {
            if (!it.isPropertyKey()) {
                throw new IllegalArgumentException("Invalid key for add operation: " + key + " (Attribute key in the middle.)");
            }
            pathNodes.add(it.currentKey());
            it.next();
        }
        return new NodeAddData<T>(parent, it.currentKey(), !it.isPropertyKey(), pathNodes);
    }

    protected <T> void findNodesForKey(DefaultConfigurationKey.KeyIterator keyPart, T node, Collection<QueryResult<T>> results, NodeHandler<T> handler) {
        if (!keyPart.hasNext()) {
            results.add(QueryResult.createNodeResult(node));
        } else {
            String key = keyPart.nextKey(false);
            if (keyPart.isPropertyKey()) {
                this.processSubNodes(keyPart, this.findChildNodesByName(handler, node, key), results, handler);
            }
            if (keyPart.isAttribute() && !keyPart.hasNext() && handler.getAttributeValue(node, key) != null) {
                results.add(QueryResult.createAttributeResult(node, key));
            }
        }
    }

    protected <T> T findLastPathNode(DefaultConfigurationKey.KeyIterator keyIt, T node, NodeHandler<T> handler) {
        String keyPart = keyIt.nextKey(false);
        if (keyIt.hasNext()) {
            int idx;
            if (!keyIt.isPropertyKey()) {
                throw new IllegalArgumentException("Invalid path for add operation: Attribute key in the middle!");
            }
            int n = idx = keyIt.hasIndex() ? keyIt.getIndex() : handler.getMatchingChildrenCount(node, this.nameMatcher, keyPart) - 1;
            if (idx < 0 || idx >= handler.getMatchingChildrenCount(node, this.nameMatcher, keyPart)) {
                return node;
            }
            return this.findLastPathNode(keyIt, this.findChildNodesByName(handler, node, keyPart).get(idx), handler);
        }
        return node;
    }

    private <T> void processSubNodes(DefaultConfigurationKey.KeyIterator keyPart, List<T> subNodes, Collection<QueryResult<T>> nodes, NodeHandler<T> handler) {
        if (keyPart.hasIndex()) {
            if (keyPart.getIndex() >= 0 && keyPart.getIndex() < subNodes.size()) {
                this.findNodesForKey((DefaultConfigurationKey.KeyIterator)keyPart.clone(), subNodes.get(keyPart.getIndex()), nodes, handler);
            }
        } else {
            subNodes.forEach(node -> this.findNodesForKey((DefaultConfigurationKey.KeyIterator)keyPart.clone(), node, nodes, handler));
        }
    }

    private <T> int determineIndex(T node, T parent, String nodeName, NodeHandler<T> handler) {
        return this.findChildNodesByName(handler, parent, nodeName).indexOf(node);
    }

    private <T> List<T> findChildNodesByName(NodeHandler<T> handler, T parent, String nodeName) {
        return handler.getMatchingChildren(parent, this.nameMatcher, nodeName);
    }
}

