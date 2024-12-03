/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.wysiwyg.ListContext;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;
import org.apache.commons.lang.StringUtils;

final class ListItemConverter
implements Converter {
    static final ListItemConverter INSTANCE = new ListItemConverter();

    private ListItemConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("li");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        NodeContext childConversionContext = new NodeContext.Builder(nodeContext).inListItem(true).ignoreText(false).previousSibling(null).build();
        String convertedChildren = wysiwygConverter.convertChildren(childConversionContext);
        String itemContent = RenderUtils.trimInitialNewline(convertedChildren);
        if (itemContent.equals("") || itemContent.trim().equals("\\\\")) {
            itemContent = "&nbsp;";
        }
        while (itemContent.endsWith("\n")) {
            itemContent = StringUtils.chomp((String)itemContent);
        }
        while (itemContent.endsWith("\\\\\n ")) {
            itemContent = StringUtils.chomp((String)itemContent, (String)"\\\\\n ");
        }
        if (itemContent.trim().startsWith("##") || itemContent.trim().startsWith("**")) {
            return wysiwygConverter.getSeparator("li", nodeContext) + itemContent;
        }
        ListContext listContext = nodeContext.getListContext();
        if (listContext == null) {
            listContext = new ListContext("*");
        }
        return wysiwygConverter.getSeparator("li", nodeContext) + listContext.decorateText(itemContent);
    }
}

