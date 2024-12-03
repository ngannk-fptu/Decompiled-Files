/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.code.formatter;

import com.atlassian.renderer.v2.macro.code.formatter.AbstractFormatter;

public class XmlFormatter
extends AbstractFormatter {
    private static final String KEYWORDS1 = "\\b(xsl:[^&\\s]*)\\b";
    private static final String KEYWORDS2 = "\\b(xmlns:[^&=\\s]*)\\b";
    private static final String TAGS = "(&lt;/?(.*?)&gt;)";
    private static final String COMMENTS = "((<|&lt;)(\\!|&#33;)--)(.+?)(--(>|&gt;))";
    private static final String XML_COMMENTS_REPLACEMENT = "<span class=\"code-comment\">$1$4$5</span>";
    private static final String TAGS_REPLACEMENT = "<span class=\"code-tag\">$1</span>";
    private static final String[] SUPPORTED_LANGUAGES = new String[]{"xml", "html", "xhtml"};

    public XmlFormatter() {
        this.addReplacement("(\"|\\&quot;)(.+?)(?<!(\"|\\&quot;))(\"|\\&quot;)", "<span class=\"code-quote\">$1$2$4</span>");
        this.addReplacement(KEYWORDS1, "<span class=\"code-keyword\">$1</span>");
        this.addReplacement(KEYWORDS2, "<span class=\"code-keyword\">$1</span>");
        this.addReplacement(TAGS, TAGS_REPLACEMENT);
        this.addReplacement(COMMENTS, XML_COMMENTS_REPLACEMENT);
    }

    @Override
    public String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}

