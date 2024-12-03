/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.ReferenceNodeHandler;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

final class XMLListReference {
    private final Element element;

    private XMLListReference(Element e) {
        this.element = e;
    }

    public Element getElement() {
        return this.element;
    }

    public static void assignListReference(Map<ImmutableNode, Object> refs, ImmutableNode node, Element elem) {
        if (refs != null) {
            refs.put(node, new XMLListReference(elem));
        }
    }

    public static boolean isListNode(ImmutableNode node, ReferenceNodeHandler handler) {
        if (XMLListReference.hasListReference(node, handler)) {
            return true;
        }
        ImmutableNode parent = handler.getParent(node);
        if (parent != null) {
            for (int i = 0; i < handler.getChildrenCount(parent, null); ++i) {
                ImmutableNode child = handler.getChild(parent, i);
                if (!XMLListReference.hasListReference(child, handler) || !XMLListReference.nameEquals(node, child)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean isFirstListItem(ImmutableNode node, ReferenceNodeHandler handler) {
        ImmutableNode parent = handler.getParent(node);
        ImmutableNode firstItem = null;
        int idx = 0;
        while (firstItem == null) {
            ImmutableNode child = handler.getChild(parent, idx);
            if (XMLListReference.nameEquals(node, child)) {
                firstItem = child;
            }
            ++idx;
        }
        return firstItem == node;
    }

    public static String listValue(ImmutableNode node, ReferenceNodeHandler nodeHandler, ListDelimiterHandler delimiterHandler) {
        ImmutableNode parent = nodeHandler.getParent(node);
        List<ImmutableNode> items = nodeHandler.getChildren(parent, node.getNodeName());
        List values = items.stream().map(ImmutableNode::getValue).collect(Collectors.toList());
        try {
            return String.valueOf(delimiterHandler.escapeList(values, ListDelimiterHandler.NOOP_TRANSFORMER));
        }
        catch (UnsupportedOperationException e) {
            throw new ConfigurationRuntimeException("List handling not supported by the current ListDelimiterHandler! Make sure that the same delimiter handler is used for loading and saving the configuration.", e);
        }
    }

    private static boolean hasListReference(ImmutableNode node, ReferenceNodeHandler handler) {
        return handler.getReference(node) instanceof XMLListReference;
    }

    private static boolean nameEquals(ImmutableNode n1, ImmutableNode n2) {
        return StringUtils.equals((CharSequence)n2.getNodeName(), (CharSequence)n1.getNodeName());
    }
}

