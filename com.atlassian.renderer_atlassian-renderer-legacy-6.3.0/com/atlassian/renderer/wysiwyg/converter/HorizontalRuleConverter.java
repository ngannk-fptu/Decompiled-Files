/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

final class HorizontalRuleConverter
implements Converter {
    static HorizontalRuleConverter INSTANCE = new HorizontalRuleConverter();

    private HorizontalRuleConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("hr");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String separator = DefaultWysiwygConverter.isUserNewline(nodeContext.getPreviousSibling()) ? "" : "\n";
        return separator + "----";
    }
}

