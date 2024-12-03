/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.opensymphony.util.TextUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class PlainTextToHtmlConverter {
    private static final Pattern INLINE_SPACES_TO_REPLACE = Pattern.compile("\\s{2,}");

    @HtmlSafe
    public static String toHtml(String plainText) {
        if (StringUtils.isEmpty((CharSequence)plainText)) {
            return "";
        }
        String html = PlainTextToHtmlConverter.encodeHtmlEntities(plainText);
        html = TextUtils.leadingSpaces((String)html);
        html = html.replaceAll("\\n", "<br>\n");
        html = TextUtils.hyperlink((String)html);
        return PlainTextToHtmlConverter.matchAndReplaceSpaces(html);
    }

    @HtmlSafe
    public static String[] encodeHtmlEntities(Object ... items) {
        if (items == null) {
            return null;
        }
        String[] result = new String[items.length];
        for (int i = 0; i < items.length; ++i) {
            result[i] = PlainTextToHtmlConverter.encodeHtmlEntities(String.valueOf(items[i]));
        }
        return result;
    }

    @HtmlSafe
    public static String[] encodeHtmlEntities(String ... items) {
        if (items == null) {
            return null;
        }
        String[] result = new String[items.length];
        for (int i = 0; i < items.length; ++i) {
            result[i] = PlainTextToHtmlConverter.encodeHtmlEntities(items[i]);
        }
        return result;
    }

    @HtmlSafe
    public static String encodeHtmlEntities(String text) {
        if (StringUtils.isEmpty((CharSequence)text)) {
            return "";
        }
        StringBuilder str = new StringBuilder(PlainTextToHtmlConverter.expectedEncodedLength(text.length()));
        block7: for (int j = 0; j < text.length(); ++j) {
            char c = text.charAt(j);
            if (c >= '\u0080') {
                str.append(c);
                continue;
            }
            switch (c) {
                case '\'': {
                    str.append("&#39;");
                    continue block7;
                }
                case '\"': {
                    str.append("&quot;");
                    continue block7;
                }
                case '&': {
                    str.append("&amp;");
                    continue block7;
                }
                case '<': {
                    str.append("&lt;");
                    continue block7;
                }
                case '>': {
                    str.append("&gt;");
                    continue block7;
                }
                default: {
                    str.append(c);
                }
            }
        }
        return str.toString();
    }

    private static int expectedEncodedLength(int originalLength) {
        return originalLength + (originalLength >> 3);
    }

    public static String matchAndReplaceSpaces(String html) {
        Matcher matcher = INLINE_SPACES_TO_REPLACE.matcher(html);
        StringBuffer result = new StringBuffer(html.length() + 100);
        while (matcher.find()) {
            matcher.appendReplacement(result, " ");
            int numberOfSpaces = matcher.group().length() - 1;
            for (int i = 0; i < numberOfSpaces; ++i) {
                result.append("&nbsp;");
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
}

