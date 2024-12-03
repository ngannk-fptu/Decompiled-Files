/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.code;

import org.radeox.macro.code.DefaultRegexCodeFormatter;
import org.radeox.macro.code.SourceCodeFormatter;

public class SqlCodeFilter
extends DefaultRegexCodeFormatter
implements SourceCodeFormatter {
    private static final String KEYWORDS = "\\b(SELECT|DELETE|UPDATE|WHERE|FROM|GROUP|BY|HAVING)\\b";
    private static final String OBJECTS = "\\b(VARCHAR)\\b";
    private static final String QUOTES = "\"(([^\"\\\\]|\\.)*)\"";

    public SqlCodeFilter() {
        super(QUOTES, "<span class=\"sql-quote\">\"$1\"</span>");
        this.addRegex(OBJECTS, "<span class=\"sql-object\">$1</span>");
        this.addRegex(KEYWORDS, "<span class=\"sql-keyword\">$1</span>");
    }

    public String getName() {
        return "sql";
    }
}

