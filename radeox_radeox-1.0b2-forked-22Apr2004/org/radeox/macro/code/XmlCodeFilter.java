/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.code;

import org.radeox.macro.code.DefaultRegexCodeFormatter;
import org.radeox.macro.code.SourceCodeFormatter;

public class XmlCodeFilter
extends DefaultRegexCodeFormatter
implements SourceCodeFormatter {
    private static final String KEYWORDS = "\\b(xsl:[^&\\s]*)\\b";
    private static final String TAGS = "(&#60;/?.*?&#62;)";
    private static final String QUOTE = "\"(([^\"\\\\]|\\.)*)\"";

    public XmlCodeFilter() {
        super(QUOTE, "<span class=\"xml-quote\">\"$1\"</span>");
        this.addRegex(TAGS, "<span class=\"xml-tag\">$1</span>");
        this.addRegex(KEYWORDS, "<span class=\"xml-keyword\">$1</span>");
    }

    public String getName() {
        return "xml";
    }
}

