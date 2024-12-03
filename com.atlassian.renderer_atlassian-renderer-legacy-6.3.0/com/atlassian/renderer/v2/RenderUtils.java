/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.opensymphony.util.TextUtils;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class RenderUtils {
    public static final String WIKI_SRC_CLASS = "wikisrc";

    public static boolean isBlank(String str) {
        for (int i = 0; i < str.length(); ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static String blockError(String message, String contents) {
        return "<div class=\"error\"><span class=\"error\">" + HtmlEscaper.escapeAll(message, true) + "</span> " + contents + "</div>";
    }

    public static String error(RenderContext context, String message, String wysiwygContents, boolean suppressMessageOnWysiwyg) {
        String wysiwyg = "";
        if (context.isRenderingForWysiwyg() && wysiwygContents != null) {
            wysiwyg = " <span class=\"wikisrc\">" + wysiwygContents + "</span>";
            if (suppressMessageOnWysiwyg) {
                message = "";
            }
        }
        return "<span class=\"error\">" + HtmlEscaper.escapeAll(message, true) + "</span>" + wysiwyg;
    }

    public static String error(String message) {
        return "<span class=\"error\">" + HtmlEscaper.escapeAll(message, true) + "</span>";
    }

    public static void tabTo(StringBuffer buf, int depth) {
        for (int i = 0; i < depth; ++i) {
            buf.append("\t");
        }
    }

    public static String getParameter(Map parameters, String key, int alternateIndex) {
        String result = (String)parameters.get(key);
        if (StringUtils.isBlank((String)result)) {
            result = TextUtils.noNull((String)((String)parameters.get(Integer.toString(alternateIndex))));
        }
        return result;
    }

    public static String trimInitialNewline(String s) {
        if (StringUtils.isEmpty((String)s) || !s.startsWith("\n")) {
            return s;
        }
        return s.substring(1);
    }

    public static String stripCarriageReturns(String s) {
        return s == null ? null : s.replaceAll("\r", "");
    }

    public static String trimNewlinesAndEscapedNewlines(String s) {
        if (s.startsWith("\n")) {
            s = s.substring(1);
        }
        if (s.endsWith("\n")) {
            s = s.substring(0, s.length() - 1);
        }
        if (s.startsWith("\\\\\n")) {
            s = s.substring(3);
        }
        if (s.endsWith("\\\\")) {
            s = s.substring(0, s.length() - 2);
        }
        return s;
    }
}

