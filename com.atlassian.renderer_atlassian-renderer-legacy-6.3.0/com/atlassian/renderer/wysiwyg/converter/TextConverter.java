/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.util.NodeUtil;
import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.WikiMarkupEscaper;
import com.atlassian.renderer.wysiwyg.converter.Converter;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

class TextConverter
implements Converter {
    public static TextConverter INSTANCE = new TextConverter();

    private TextConverter() {
    }

    @Override
    public boolean canConvert(NodeContext nodeContext) {
        return NodeUtil.isTextNode(nodeContext.getNode());
    }

    @Override
    public String convertNode(NodeContext nodeContext, DefaultWysiwygConverter wysiwygConverter) {
        if (nodeContext.isIgnoreText()) {
            return null;
        }
        String s = nodeContext.getNode().getNodeValue();
        if (this.isWhitespaceOnly(s)) {
            return null;
        }
        s = s.replaceAll("^\n", "");
        s = s.replaceAll("\n$", "");
        if ((s = s.replaceAll("(\n|\r)", " ")).trim().equals("")) {
            return " ";
        }
        StringBuffer sb = new StringBuffer();
        if (s.startsWith(" ")) {
            sb.append("TEXTSEP");
        }
        String sTrimmed = s.trim();
        if (!DefaultWysiwygConverter.debug) {
            sTrimmed = WikiMarkupEscaper.escapeWikiMarkup(sTrimmed);
        }
        for (com.atlassian.renderer.v2.components.TextConverter textConverter : wysiwygConverter.getTextConverterComponents()) {
            sTrimmed = textConverter.convertToWikiMarkup(sTrimmed);
        }
        sb.append(nodeContext.getStyles().decorateText(sTrimmed));
        if (!s.equals(" ") && s.endsWith(" ")) {
            sb.append("TEXTSEP");
        }
        return wysiwygConverter.getSeparator("text", nodeContext) + this.replaceEntities(sb.toString());
    }

    private boolean isWhitespaceOnly(String string) {
        return string.replaceAll("(\n|\r|\t)+ *", "").length() == 0;
    }

    private String replaceEntities(String s) {
        return s.replaceAll("\\u2014", "---").replaceAll("\\u2013", "--").replaceAll("\\u00A0", "&nbsp;");
    }
}

