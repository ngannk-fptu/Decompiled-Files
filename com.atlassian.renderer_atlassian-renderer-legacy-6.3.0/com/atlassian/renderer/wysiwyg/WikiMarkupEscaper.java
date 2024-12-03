/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg;

public class WikiMarkupEscaper {
    private static String[] escapedCharactersRegex = new String[]{"(\\\\)?\\[", "(\\\\)?\\]", "([^\\s])([\\*\\-\\+\\^~_](?:[\\s]|$))", "((?:^|[\\s]))([\\*\\-\\+\\^~_][^\\s])", "(\\\\)?!", "(\\\\)?\\|", "(^\\s*)([\\*\\#\\-])"};
    private static String[] escapedCharactersReplacement = new String[]{"\\\\[", "\\\\]", "$1\\\\$2", "$1\\\\$2", "\\\\!", "\\\\|", "$1\\\\$2"};

    public static String escapeWikiMarkup(String s) {
        for (int i = 0; i < escapedCharactersRegex.length; ++i) {
            s = s.replaceAll(escapedCharactersRegex[i], escapedCharactersReplacement[i]);
        }
        return s;
    }
}

