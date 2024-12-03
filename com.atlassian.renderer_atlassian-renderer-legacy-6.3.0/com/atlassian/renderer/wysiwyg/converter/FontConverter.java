/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

class FontConverter
implements Converter {
    static final FontConverter INSTANCE = new FontConverter();

    FontConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.getNode().getNodeName().equalsIgnoreCase("font");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        NodeContext noIgnoreTextAndNoPreviousSibling = new NodeContext.Builder(nodeContext).ignoreText(false).previousSibling(null).build();
        return wysiwygConverter.convertChildren(noIgnoreTextAndNoPreviousSibling);
    }
}

