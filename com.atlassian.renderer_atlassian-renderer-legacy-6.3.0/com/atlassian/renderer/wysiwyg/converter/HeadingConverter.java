/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

final class HeadingConverter
implements Converter {
    static HeadingConverter INSTANCE = new HeadingConverter();

    private HeadingConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return DefaultWysiwygConverter.isHeading(nodeContext.getNodeName());
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String separator = (DefaultWysiwygConverter.isUserNewline(nodeContext.getPreviousSibling()) ? "" : "\n") + wysiwygConverter.getSeparator("heading", nodeContext);
        NodeContext childConversionContext = new NodeContext.Builder(nodeContext).ignoreText(false).previousSibling(null).inHeading(true).build();
        String convertedChildren = wysiwygConverter.convertChildren(childConversionContext);
        return separator + nodeContext.getNodeName() + ". " + convertedChildren;
    }
}

