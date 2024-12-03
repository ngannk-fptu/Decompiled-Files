/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.ListContext;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import org.w3c.dom.Node;

final class ListConverter
implements Converter {
    static final ListConverter INSTANCE = new ListConverter();

    private ListConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("ol") || nodeContext.hasNodeName("ul");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String separator = wysiwygConverter.getSeparator("list", nodeContext);
        NodeContext.Builder builder = new NodeContext.Builder(nodeContext).ignoreText(true).previousSibling(null);
        String listType = ListConverter.getListType(nodeContext);
        if (ListConverter.isNewList(nodeContext.getNode().getParentNode())) {
            builder.listContext(new ListContext(listType));
        } else {
            builder.listContext(new ListContext(listType, nodeContext.getListContext()));
        }
        return separator + wysiwygConverter.convertChildren(builder.build()).trim();
    }

    private static String getListType(NodeContext nodeContext) {
        if (nodeContext.hasNodeName("ol")) {
            return "#";
        }
        String typeAttribute = nodeContext.getAttribute("type");
        if (typeAttribute != null && typeAttribute.equals("square")) {
            return "-";
        }
        return "*";
    }

    private static boolean isNewList(Node parentNode) {
        if (parentNode == null) {
            return false;
        }
        String parentNodeName = parentNode.getNodeName().toLowerCase();
        return !parentNodeName.equals("li") && !parentNodeName.equals("ul") && !parentNodeName.equals("ol");
    }
}

