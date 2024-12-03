/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

class BreakConverter
implements Converter {
    public static BreakConverter INSTANCE = new BreakConverter();

    private BreakConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.getNode().getNodeName().equalsIgnoreCase("br");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        if (DefaultWysiwygConverter.isForcedNewline(nodeContext.getNode())) {
            return wysiwygConverter.getSeparator("forcedNewline", nodeContext) + "\\\\ ";
        }
        return wysiwygConverter.getSeparator("br", nodeContext);
    }
}

