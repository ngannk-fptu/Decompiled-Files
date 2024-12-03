/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

final class TableBodyConverter
implements Converter {
    static TableBodyConverter INSTANCE = new TableBodyConverter();

    private TableBodyConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("tbody");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        NodeContext childConversionContext = new NodeContext.Builder(nodeContext).ignoreText(true).previousSibling(null).build();
        return wysiwygConverter.convertChildren(childConversionContext);
    }
}

