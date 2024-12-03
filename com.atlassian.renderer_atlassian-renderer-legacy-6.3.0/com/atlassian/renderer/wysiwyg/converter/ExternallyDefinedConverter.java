/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.WysiwygNodeConverter;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

class ExternallyDefinedConverter
implements Converter {
    public static ExternallyDefinedConverter INSTANCE = new ExternallyDefinedConverter();

    private ExternallyDefinedConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.getAttribute("wysiwyg") != null;
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String converterName = nodeContext.getAttribute("wysiwyg");
        if (converterName.equals("ignore")) {
            return "";
        }
        WysiwygNodeConverter wysiwygNodeConverter = wysiwygConverter.findNodeConverter(converterName);
        NodeContext modified = new NodeContext.Builder(nodeContext).ignoreText(false).build();
        return modified.invokeConvert(wysiwygNodeConverter, wysiwygConverter);
    }
}

