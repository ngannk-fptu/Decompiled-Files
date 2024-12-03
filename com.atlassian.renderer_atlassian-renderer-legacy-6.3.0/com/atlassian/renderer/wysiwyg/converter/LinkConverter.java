/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.Styles;
import com.atlassian.renderer.wysiwyg.WysiwygLinkHelper;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

final class LinkConverter
implements Converter {
    static LinkConverter INSTANCE = new LinkConverter();

    private LinkConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName("a");
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        if (nodeContext.getAttribute("linktype") == null) {
            String href = nodeContext.getAttribute("href");
            String alias = DefaultWysiwygConverter.getRawChildText(nodeContext.getNode(), true);
            return href != null ? "[" + alias + "|" + href + "]" : alias;
        }
        String separator = wysiwygConverter.getSeparator("a", nodeContext);
        NodeContext childConversionContext = new NodeContext.Builder(nodeContext).styles(new Styles()).ignoreText(false).escapeWikiMarkup(false).build();
        String newAlias = wysiwygConverter.convertChildren(childConversionContext);
        String linkWikiText = WysiwygLinkHelper.createLinkWikiText(nodeContext.getNode(), newAlias);
        return separator + nodeContext.getStyles().decorateText(linkWikiText);
    }
}

