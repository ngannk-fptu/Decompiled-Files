/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.util;

import com.opensymphony.util.TextUtils;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RendererUtil {
    private static final Logger log = LoggerFactory.getLogger(RendererUtil.class);

    public static String summarise(String content) {
        if (!TextUtils.stringSet((String)content)) {
            return content;
        }
        int urlIdx = (content = RendererUtil.stripBasicMarkup(content)).indexOf("http://");
        if (urlIdx > 0) {
            content = content.substring(0, urlIdx);
        }
        return RendererUtil.summariseWithoutStrippingWikiCharacters(content).trim();
    }

    public static String stripBasicMarkup(String content) {
        if (!TextUtils.stringSet((String)content)) {
            return content;
        }
        content = content.replaceAll("h[0-9]\\.", " ");
        content = content.replaceAll("[\\[\\]\\*_\\^\\-\\~\\+]", "");
        content = content.replaceAll("\\|", " ");
        content = content.replaceAll("\\{[^:\\}\\{]+?(?::[^\\}\\{]*?)?\\}(?!\\})", " ");
        content = content.replaceAll("\\n", " ");
        content = content.replaceAll("\\r", " ");
        content = content.replaceAll("bq\\.", " ");
        content = content.replaceAll("  ", " ");
        return content;
    }

    public static String summariseWithoutStrippingWikiCharacters(String content) {
        if (!TextUtils.stringSet((String)content)) {
            return content;
        }
        StringTokenizer st = new StringTokenizer(content, " ");
        if (st.countTokens() == 1) {
            if (content != null && content.length() > 50) {
                content = content.substring(0, 46);
                content = content.concat(" ...");
            }
        } else if (content != null && content.length() > 255) {
            content = TextUtils.trimToEndingChar((String)content, (int)251) + "...";
        }
        return content;
    }

    public static void appendAttribute(String name, String value, StringBuffer buffer) {
        buffer.append(name).append("=").append("\"").append(value).append("\" ");
    }

    public static void appendAttribute(String name, boolean value, StringBuffer buffer) {
        RendererUtil.appendAttribute(name, String.valueOf(value), buffer);
    }
}

