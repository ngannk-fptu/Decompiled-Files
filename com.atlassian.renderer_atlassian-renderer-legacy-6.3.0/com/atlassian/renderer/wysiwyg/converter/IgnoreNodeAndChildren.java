/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

final class IgnoreNodeAndChildren
implements Converter {
    static IgnoreNodeAndChildren INSTANCE = new IgnoreNodeAndChildren();

    private IgnoreNodeAndChildren() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("link");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        return "";
    }
}

