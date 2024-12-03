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

enum TableCellConverter implements Converter
{
    TH("th", "||"),
    TD("td", "|");

    private final String nodeName;
    private final String delimiter;

    private TableCellConverter(String nodeName, String delimiter) {
        this.nodeName = nodeName;
        this.delimiter = delimiter;
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return nodeContext.hasNodeName(this.nodeName);
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        NodeContext childConversionContext = new NodeContext.Builder(nodeContext).inTable(true).ignoreText(false).previousSibling(null).build();
        String convertedChildren = wysiwygConverter.convertChildren(childConversionContext);
        return this.delimiter + TableCellConverter.normaliseCellPadding(convertedChildren);
    }

    static String normaliseCellPadding(String s) {
        String trimmed = TableCellConverter.trimWhitespace(s);
        trimmed = trimmed.replaceAll("^&nbsp;", "");
        if ((trimmed = trimmed.replaceAll("&nbsp;$", "")).equals("&nbsp;") || StringUtils.isEmpty((String)trimmed)) {
            return " ";
        }
        return " " + trimmed + " ";
    }

    private static String trimWhitespace(String s) {
        return s.replace('\u00a0', ' ').trim();
    }
}

