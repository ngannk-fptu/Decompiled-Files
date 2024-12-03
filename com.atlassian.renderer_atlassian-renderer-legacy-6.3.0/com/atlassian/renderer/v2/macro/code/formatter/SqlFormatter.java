/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.code.formatter;

import com.atlassian.renderer.v2.macro.code.formatter.AbstractFormatter;

public class SqlFormatter
extends AbstractFormatter {
    private static final String KEYWORDS = "(?i)\\b(SELECT|DELETE|UPDATE|WHERE|FROM|GROUP|BY|HAVING)\\b";
    private static final String OBJECTS = "\\b(VARCHAR)\\b";
    private static final String COMMENTS = "^\\s*--.*";
    private static final String[] SUPPORTED_LANGUAGES = new String[]{"sql"};

    public SqlFormatter() {
        this.addReplacement("(\"|\\&quot;)(.+?)(?<!(\"|\\&quot;))(\"|\\&quot;)", "<span class=\"code-quote\">$1$2$4</span>");
        this.addReplacement(COMMENTS, "<span class=\"code-comment\">$0</span>");
        this.addReplacement(OBJECTS, "<span class=\"code-object\">$1</span>");
        this.addReplacement(KEYWORDS, "<span class=\"code-keyword\">$1</span>");
    }

    @Override
    public String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}

