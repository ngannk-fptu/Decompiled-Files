/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.code.formatter;

import com.atlassian.renderer.v2.macro.code.formatter.AbstractFormatter;

public class JavaFormatter
extends AbstractFormatter {
    private static final String KEYWORDS = "\\b(abstract|assert|break|byvalue|case|cast|catch|const|continue|default|do|else|enum|extends|false|final|finally|for|future|generic|goto|if|implements|import|inner|instanceof|interface|native|new|null|operator|outer|package|private|protected|public|rest|return|static|super|switch|synchronized|this|throw|throws|transient|true|try|var|volatile|while)\\b";
    private static final String OBJECTS = "\\b(Boolean|Byte|Character|Class|ClassLoader|Cloneable|Compiler|Double|Float|Integer|Long|Math|Number|Object|Process|Runnable|Runtime|SecurityManager|Short|String|StringBuffer|System|Thread|ThreadGroup|Void|boolean|char|byte|short|int|long|float|double)\\b";
    private static final String[] SUPPORTED_LANGUAGES = new String[]{"java"};

    public JavaFormatter() {
        this.addReplacement("(\"|\\&quot;)(.+?)(?<!(\"|\\&quot;))(\"|\\&quot;)", "<span class=\"code-quote\">$1$2$4</span>");
        this.addReplacement("//(.*?)($|\r?\n)", "<span class=\"code-comment\">$0</span>");
        this.addReplacement(KEYWORDS, "<span class=\"code-keyword\">$1</span>");
        this.addReplacement(OBJECTS, "<span class=\"code-object\">$1</span>");
    }

    @Override
    public String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }
}

