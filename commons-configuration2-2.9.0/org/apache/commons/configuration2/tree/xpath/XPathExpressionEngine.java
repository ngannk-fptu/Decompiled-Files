/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.jxpath.JXPathContext
 *  org.apache.commons.jxpath.ri.JXPathContextReferenceImpl
 *  org.apache.commons.jxpath.ri.model.NodePointerFactory
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.tree.xpath;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.tree.ExpressionEngine;
import org.apache.commons.configuration2.tree.NodeAddData;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.QueryResult;
import org.apache.commons.configuration2.tree.xpath.ConfigurationNodePointerFactory;
import org.apache.commons.configuration2.tree.xpath.XPathContextFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;
import org.apache.commons.jxpath.ri.model.NodePointerFactory;
import org.apache.commons.lang3.StringUtils;

public class XPathExpressionEngine
implements ExpressionEngine {
    static final String PATH_DELIMITER = "/";
    static final String ATTR_DELIMITER = "@";
    private static final String NODE_PATH_DELIMITERS = "/@";
    private static final String SPACE = " ";
    private static final int BUF_SIZE = 128;
    private static final char START_INDEX = '[';
    private static final char END_INDEX = ']';
    private final XPathContextFactory contextFactory;

    public XPathExpressionEngine() {
        this(new XPathContextFactory());
    }

    XPathExpressionEngine(XPathContextFactory factory) {
        this.contextFactory = factory;
    }

    @Override
    public <T> List<QueryResult<T>> query(T root, String key, NodeHandler<T> handler) {
        if (StringUtils.isEmpty((CharSequence)key)) {
            QueryResult<T> result = XPathExpressionEngine.createResult(root);
            return Collections.singletonList(result);
        }
        JXPathContext context = this.createContext(root, handler);
        List results = context.selectNodes(key);
        if (results == null) {
            results = Collections.emptyList();
        }
        return XPathExpressionEngine.convertResults(results);
    }

    @Override
    public <T> String nodeKey(T node, String parentKey, NodeHandler<T> handler) {
        if (parentKey == null) {
            return "";
        }
        if (handler.nodeName(node) == null) {
            return parentKey;
        }
        StringBuilder buf = new StringBuilder(parentKey.length() + handler.nodeName(node).length() + PATH_DELIMITER.length());
        if (!parentKey.isEmpty()) {
            buf.append(parentKey);
            buf.append(PATH_DELIMITER);
        }
        buf.append(handler.nodeName(node));
        return buf.toString();
    }

    @Override
    public String attributeKey(String parentKey, String attributeName) {
        StringBuilder buf = new StringBuilder(StringUtils.length((CharSequence)parentKey) + StringUtils.length((CharSequence)attributeName) + PATH_DELIMITER.length() + ATTR_DELIMITER.length());
        if (StringUtils.isNotEmpty((CharSequence)parentKey)) {
            buf.append(parentKey).append(PATH_DELIMITER);
        }
        buf.append(ATTR_DELIMITER).append(attributeName);
        return buf.toString();
    }

    @Override
    public <T> String canonicalKey(T node, String parentKey, NodeHandler<T> handler) {
        T parent = handler.getParent(node);
        if (parent == null) {
            return StringUtils.defaultString((String)parentKey);
        }
        StringBuilder buf = new StringBuilder(128);
        if (StringUtils.isNotEmpty((CharSequence)parentKey)) {
            buf.append(parentKey).append(PATH_DELIMITER);
        }
        buf.append(handler.nodeName(node));
        buf.append('[');
        buf.append(XPathExpressionEngine.determineIndex(parent, node, handler));
        buf.append(']');
        return buf.toString();
    }

    @Override
    public <T> NodeAddData<T> prepareAdd(T root, String key, NodeHandler<T> handler) {
        if (key == null) {
            throw new IllegalArgumentException("prepareAdd: key must not be null!");
        }
        String addKey = key;
        int index = XPathExpressionEngine.findKeySeparator(addKey);
        if (index < 0) {
            addKey = this.generateKeyForAdd(root, addKey, handler);
            index = XPathExpressionEngine.findKeySeparator(addKey);
        } else if (index >= addKey.length() - 1) {
            XPathExpressionEngine.invalidPath(addKey, " new node path must not be empty.");
        }
        List<QueryResult<T>> nodes = this.query(root, addKey.substring(0, index).trim(), handler);
        if (nodes.size() != 1) {
            throw new IllegalArgumentException("prepareAdd: key '" + key + "' must select exactly one target node!");
        }
        return this.createNodeAddData(addKey.substring(index).trim(), nodes.get(0));
    }

    private <T> JXPathContext createContext(T root, NodeHandler<T> handler) {
        return this.getContextFactory().createContext(root, handler);
    }

    <T> NodeAddData<T> createNodeAddData(String path, QueryResult<T> parentNodeResult) {
        if (parentNodeResult.isAttributeResult()) {
            XPathExpressionEngine.invalidPath(path, " cannot add properties to an attribute.");
        }
        LinkedList<String> pathNodes = new LinkedList<String>();
        String lastComponent = null;
        boolean attr = false;
        boolean first = true;
        StringTokenizer tok = new StringTokenizer(path, NODE_PATH_DELIMITERS, true);
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            if (PATH_DELIMITER.equals(token)) {
                if (attr) {
                    XPathExpressionEngine.invalidPath(path, " contains an attribute delimiter at a disallowed position.");
                }
                if (lastComponent == null) {
                    XPathExpressionEngine.invalidPath(path, " contains a '/' at a disallowed position.");
                }
                pathNodes.add(lastComponent);
                lastComponent = null;
            } else if (ATTR_DELIMITER.equals(token)) {
                if (attr) {
                    XPathExpressionEngine.invalidPath(path, " contains multiple attribute delimiters.");
                }
                if (lastComponent == null && !first) {
                    XPathExpressionEngine.invalidPath(path, " contains an attribute delimiter at a disallowed position.");
                }
                if (lastComponent != null) {
                    pathNodes.add(lastComponent);
                }
                attr = true;
                lastComponent = null;
            } else {
                lastComponent = token;
            }
            first = false;
        }
        if (lastComponent == null) {
            XPathExpressionEngine.invalidPath(path, "contains no components.");
        }
        return new NodeAddData<T>(parentNodeResult.getNode(), lastComponent, attr, pathNodes);
    }

    XPathContextFactory getContextFactory() {
        return this.contextFactory;
    }

    private <T> String generateKeyForAdd(T root, String key, NodeHandler<T> handler) {
        int pos = key.lastIndexOf(PATH_DELIMITER, key.length());
        while (pos >= 0) {
            String keyExisting = key.substring(0, pos);
            if (!this.query(root, keyExisting, handler).isEmpty()) {
                StringBuilder buf = new StringBuilder(key.length() + 1);
                buf.append(keyExisting).append(SPACE);
                buf.append(key.substring(pos + 1));
                return buf.toString();
            }
            pos = key.lastIndexOf(PATH_DELIMITER, pos - 1);
        }
        return SPACE + key;
    }

    private static <T> int determineIndex(T parent, T child, NodeHandler<T> handler) {
        return handler.getChildren(parent, handler.nodeName(child)).indexOf(child) + 1;
    }

    private static void invalidPath(String path, String msg) {
        throw new IllegalArgumentException("Invalid node path: \"" + path + "\" " + msg);
    }

    private static int findKeySeparator(String key) {
        int index;
        for (index = key.length() - 1; index >= 0 && !Character.isWhitespace(key.charAt(index)); --index) {
        }
        return index;
    }

    private static <T> List<QueryResult<T>> convertResults(List<?> results) {
        return results.stream().map(res -> XPathExpressionEngine.createResult(res)).collect(Collectors.toList());
    }

    private static <T> QueryResult<T> createResult(Object resObj) {
        if (resObj instanceof QueryResult) {
            return (QueryResult)resObj;
        }
        return QueryResult.createNodeResult(resObj);
    }

    static {
        JXPathContextReferenceImpl.addNodePointerFactory((NodePointerFactory)new ConfigurationNodePointerFactory());
    }
}

