/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.util.UrlUtil
 *  org.apache.commons.lang3.CharUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.util.UrlUtil;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

public class UrlUtils {
    public static final String URL_PATTERN;
    private static final Pattern ENDS_WITH_PUNCTUATION;
    private static final Set<Character> ILLEGAL_URL_TITLE_CHARS;

    public static boolean isSameOrigin(URL url, URL origin) {
        if (url == null || origin == null) {
            return false;
        }
        return origin.getProtocol().equals(url.getProtocol()) && origin.getPort() == url.getPort() && origin.getHost().equals(url.getHost());
    }

    public static String addContextPath(String url, ContextPathHolder contextPathHolder) {
        String context = contextPathHolder.getContextPath();
        if (UrlUtils.isAbsoluteUrl(url) || StringUtils.startsWith((CharSequence)url, (CharSequence)context)) {
            return url;
        }
        if (url.startsWith("/")) {
            return context + url;
        }
        return context + "/" + url;
    }

    public static boolean verifyUrl(String url) {
        try {
            new URL(url);
            return url.matches(URL_PATTERN);
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean isAbsoluteUrl(String url) {
        return url.startsWith("http");
    }

    public static String removeUrlsFromString(String text) {
        return text != null ? text.replaceAll(URL_PATTERN, "") : "";
    }

    public static boolean isResumeDraftUrl(String url) {
        return url.contains("resumedraft.");
    }

    public static boolean isPageCreationUrl(String url) {
        return url.contains("createpage.") || url.contains("createpage-entervariables.") || url.contains("doenterpagevariables.") || url.contains("copypage");
    }

    public static boolean isPageEditUrl(String url) {
        return url.contains("editpage.");
    }

    public static boolean isBlogPostCreationUrl(String url) {
        return url.contains("createblog");
    }

    public static boolean isBlogPostEditUrl(String url) {
        return url.contains("editblog");
    }

    public static boolean isTemplateCreationUrl(String url) {
        return url.contains("createpagetemplate");
    }

    public static boolean isTemplateEditUrl(String url) {
        return url.contains("editpagetemplate");
    }

    public static boolean isEditorLoaderUrl(String url) {
        return url.contains("editor-loader");
    }

    public static boolean isEditingUrl(String url) {
        return UrlUtils.isPageCreationUrl(url) || UrlUtils.isPageEditUrl(url) || UrlUtils.isBlogPostCreationUrl(url) || UrlUtils.isBlogPostEditUrl(url) || UrlUtils.isTemplateCreationUrl(url) || UrlUtils.isTemplateEditUrl(url) || UrlUtils.isEditorLoaderUrl(url) || UrlUtils.isResumeDraftUrl(url);
    }

    public static String getJdbcUrlQuery(String jdbcUrl) {
        try {
            URI uri = new URI(jdbcUrl.replaceAll("jdbc:", ""));
            return uri.getQuery();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isSafeTitleForUrl(String title) {
        if (StringUtils.isEmpty((CharSequence)title) || title.length() >= 150) {
            return false;
        }
        if (ENDS_WITH_PUNCTUATION.matcher(title).find()) {
            return false;
        }
        if (".".equals(title)) {
            return false;
        }
        for (int i = 0; i < title.length(); ++i) {
            char c = title.charAt(i);
            if (CharUtils.isAscii((char)c) && !ILLEGAL_URL_TITLE_CHARS.contains(Character.valueOf(c))) continue;
            return false;
        }
        return true;
    }

    @HtmlSafe
    public static String appendAmpersandOrQuestionMark(String str) {
        if (StringUtils.isEmpty((CharSequence)str) || str.endsWith("?")) {
            return str;
        }
        if (str.contains("?")) {
            return str + "&";
        }
        return str + "?";
    }

    static {
        ENDS_WITH_PUNCTUATION = Pattern.compile("\\p{Punct}$");
        ILLEGAL_URL_TITLE_CHARS = Set.of(Character.valueOf('+'), Character.valueOf('?'), Character.valueOf('%'), Character.valueOf('&'), Character.valueOf('\"'), Character.valueOf('/'), Character.valueOf('\\'), Character.valueOf(';'), Character.valueOf('#'));
        String protocols = StringUtils.join((Iterable)UrlUtil.URL_PROTOCOLS, (char)'|');
        URL_PATTERN = "((" + protocols + ")(%[\\p{Digit}A-Fa-f][\\p{Digit}A-Fa-f]|[-_.!~*';/?:@#&=+$,\\p{Alnum}\\[\\]\\\\])+)";
    }
}

