/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import org.apache.commons.lang.StringUtils;

final class TableRowConverter
implements Converter {
    static TableRowConverter INSTANCE = new TableRowConverter();

    private TableRowConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("tr");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        String separator = wysiwygConverter.getSeparator("tr", nodeContext);
        NodeContext childConversionContext = new NodeContext.Builder(nodeContext).inTable(true).ignoreText(true).previousSibling(null).build();
        String convertedChildren = wysiwygConverter.convertChildren(childConversionContext);
        if (StringUtils.isEmpty((String)convertedChildren)) {
            return "";
        }
        convertedChildren = convertedChildren + (convertedChildren.startsWith("||") ? "||" : "|");
        return separator + convertedChildren;
    }
}

