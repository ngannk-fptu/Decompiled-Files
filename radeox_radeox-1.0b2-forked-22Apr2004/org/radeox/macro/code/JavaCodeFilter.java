/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.code;

import org.radeox.macro.code.DefaultRegexCodeFormatter;
import org.radeox.macro.code.SourceCodeFormatter;

public class JavaCodeFilter
extends DefaultRegexCodeFormatter
implements SourceCodeFormatter {
    private static final String KEYWORDS = "\\b(abstract|break|byvalue|case|cast|catch|const|continue|default|do|else|extends|false|final|finally|for|future|generic|goto|if|implements|import|inner|instanceof|interface|native|new|null|operator|outer|package|private|protected|public|rest|return|static|super|switch|synchronized|this|throw|throws|transient|true|try|var|volatile|while)\\b";
    private static final String OBJECTS = "\\b(Boolean|Byte|Character|Class|ClassLoader|Cloneable|Compiler|Double|Float|Integer|Long|Math|Number|Object|Process|Runnable|Runtime|SecurityManager|Short|String|StringBuffer|System|Thread|ThreadGroup|Void|boolean|char|byte|short|int|long|float|double)\\b";
    private static final String QUOTES = "\"(([^\"\\\\]|\\.)*)\"";

    public JavaCodeFilter() {
        super(QUOTES, "<span class=\"java-quote\">\"$1\"</span>");
        this.addRegex(KEYWORDS, "<span class=\"java-keyword\">$1</span>");
        this.addRegex(OBJECTS, "<span class=\"java-object\">$1</span>");
    }

    public String getName() {
        return "java";
    }
}

