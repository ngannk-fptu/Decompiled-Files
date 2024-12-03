/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.renderer.v2.components;

import com.google.common.base.Function;

public class WikiLinkTransformer {
    public static final char START_LINK_CHAR = '[';
    private static final char ESCAPE_CHAR = '\\';
    private static final char END_LINK_CHAR = ']';
    private static final char NEW_LINE_CHAR = '\n';

    public String transform(String wiki, Function<String, CharSequence> transformer) {
        if (wiki == null || wiki.length() < 3) {
            return wiki;
        }
        StringBuilder result = new StringBuilder(wiki.length());
        char[] wikiChars = wiki.toCharArray();
        boolean inLink = false;
        StringBuilder linkText = new StringBuilder(20);
        char prev = '\u0000';
        for (int i = 0; i < wikiChars.length; ++i) {
            char c = wikiChars[i];
            if ('[' == c) {
                if (inLink) {
                    if (prev == '\\') {
                        linkText.append(c);
                    } else {
                        result.append((CharSequence)linkText);
                        linkText.setLength(0);
                        linkText.append(c);
                    }
                } else if (prev == '\\') {
                    result.append(c);
                } else {
                    inLink = true;
                    linkText.append(c);
                }
            } else if (']' == c && inLink) {
                if (prev == '\\') {
                    linkText.append(c);
                } else {
                    inLink = false;
                    if (linkText.length() == 1) {
                        result.append((CharSequence)linkText);
                        result.append(c);
                    } else {
                        String linkBody = linkText.substring(1);
                        result.append((CharSequence)transformer.apply((Object)linkBody));
                    }
                    linkText.setLength(0);
                }
            } else if (Character.isWhitespace(c) && '[' == prev) {
                inLink = false;
                result.append((CharSequence)linkText);
                result.append(c);
                linkText.setLength(0);
            } else if ('\n' == c && inLink) {
                inLink = false;
                result.append((CharSequence)linkText);
                result.append(c);
                linkText.setLength(0);
            } else if (!inLink) {
                result.append(c);
            } else {
                linkText.append(c);
            }
            prev = c;
        }
        if (linkText.length() > 0) {
            result.append((CharSequence)linkText);
        }
        return result.toString();
    }
}

