/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

final class PreformattingConverter
implements Converter {
    static PreformattingConverter INSTANCE = new PreformattingConverter();

    private PreformattingConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("pre") || nodeContext.hasNodeName("textarea");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String separator = wysiwygConverter.getSeparator("pre", nodeContext);
        String convertedChildren = DefaultWysiwygConverter.getRawChildText(nodeContext.getNode(), false);
        return separator + convertedChildren;
    }
}

