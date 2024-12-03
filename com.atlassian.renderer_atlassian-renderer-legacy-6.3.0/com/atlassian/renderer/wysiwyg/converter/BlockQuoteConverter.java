/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import org.w3c.dom.Node;

final class BlockQuoteConverter
implements Converter {
    static BlockQuoteConverter INSTANCE = new BlockQuoteConverter();

    private BlockQuoteConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("blockquote");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        NodeContext.Builder childContextBuilder = new NodeContext.Builder(nodeContext).ignoreText(false).previousSibling(null);
        if (nodeContext.getAttribute("markup") == null) {
            return wysiwygConverter.convertChildren(childContextBuilder.build());
        }
        Node node = nodeContext.getNode();
        if (node.getChildNodes().getLength() == 1) {
            childContextBuilder.node(node.getChildNodes().item(0));
        }
        String convertedChildren = wysiwygConverter.convertChildren(childContextBuilder.build());
        return "\nbq. " + convertedChildren;
    }
}

