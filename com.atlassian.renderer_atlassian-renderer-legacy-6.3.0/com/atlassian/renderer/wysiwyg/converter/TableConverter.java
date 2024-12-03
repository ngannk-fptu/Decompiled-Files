/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

final class TableConverter
implements Converter {
    static TableConverter INSTANCE = new TableConverter();

    private TableConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("table");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String separator = wysiwygConverter.getSeparator("table", nodeContext);
        NodeContext childConversionContext = new NodeContext.Builder(nodeContext).ignoreText(true).previousSibling(null).build();
        String convertedChildren = wysiwygConverter.convertChildren(childConversionContext);
        return separator + convertedChildren;
    }
}

