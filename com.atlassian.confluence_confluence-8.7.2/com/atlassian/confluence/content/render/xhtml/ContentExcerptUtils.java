/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.util.HTMLSearchableTextUtil;
import org.xml.sax.SAXException;

public final class ContentExcerptUtils {
    public static String extractTextFromXhtmlContent(String content) {
        String text;
        try {
            text = HTMLSearchableTextUtil.stripTags(content);
        }
        catch (SAXException e) {
            text = content;
        }
        if (text == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(text.trim());
        int i = 0;
        while ((i = sb.indexOf("\n", i)) != -1) {
            if (i + 1 < sb.length() && (sb.charAt(i + 1) == '\n' || Character.isWhitespace(sb.charAt(i + 1)))) {
                sb.delete(i + 1, i + 2);
                continue;
            }
            ++i;
        }
        return sb.toString();
    }

    public static String extractTextSummaryFromXhtmlContent(String content, int minLength, int maxLength) {
        int i;
        if (minLength > maxLength) {
            throw new IllegalArgumentException("minLength is greater than maxLength");
        }
        String text = ContentExcerptUtils.extractTextFromXhtmlContent(content);
        if (text == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(text.trim());
        while ((i = sb.indexOf("\n")) != -1 && i < minLength) {
            sb.replace(i, i + 1, i == 0 ? "" : " ");
        }
        if (i == -1) {
            i = sb.length();
        }
        sb.delete(Math.min(i, maxLength), sb.length());
        return sb.toString();
    }
}

