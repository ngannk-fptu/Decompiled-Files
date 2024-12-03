/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

class IgnoreNodeAndConvertChildText
implements Converter {
    public static IgnoreNodeAndConvertChildText INSTANCE = new IgnoreNodeAndConvertChildText();

    private IgnoreNodeAndConvertChildText() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        short nodeType = nodeContext.getNode().getNodeType();
        return nodeType == 7 || nodeType == 11;
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        NodeContext modifiedContext = new NodeContext.Builder(nodeContext).ignoreText(false).previousSibling(null).build();
        return wysiwygConverter.convertChildren(modifiedContext);
    }
}

