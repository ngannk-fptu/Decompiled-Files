/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.shared.restricted;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.template.soy.data.Dir;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.NumberData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.internal.base.CharEscapers;
import com.google.template.soy.shared.restricted.EscapingConventions;
import com.google.template.soy.shared.restricted.TagWhitelist;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

public final class Sanitizers {
    private static final Logger LOGGER = Logger.getLogger(Sanitizers.class.getName());
    private static final Set<String> HTML5_VOID_ELEMENTS = ImmutableSet.of((Object)"area", (Object)"base", (Object)"br", (Object)"col", (Object)"command", (Object)"embed", (Object[])new String[]{"hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"});

    private Sanitizers() {
    }

    public static String escapeHtml(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.HTML)) {
            return value.coerceToString();
        }
        return Sanitizers.escapeHtml(value.coerceToString());
    }

    public static String escapeHtml(String value) {
        return EscapingConventions.EscapeHtml.INSTANCE.escape(value);
    }

    public static SanitizedContent cleanHtml(SoyValue value) {
        Dir valueDir = null;
        if (value instanceof SanitizedContent) {
            SanitizedContent sanitizedContent = (SanitizedContent)value;
            if (sanitizedContent.getContentKind() == SanitizedContent.ContentKind.HTML) {
                return (SanitizedContent)value;
            }
            valueDir = sanitizedContent.getContentDirection();
        }
        return Sanitizers.cleanHtml(value.coerceToString(), valueDir);
    }

    public static SanitizedContent cleanHtml(String value) {
        return Sanitizers.cleanHtml(value, null);
    }

    public static SanitizedContent cleanHtml(String value, Dir contentDir) {
        return UnsafeSanitizedContentOrdainer.ordainAsSafe(Sanitizers.stripHtmlTags(value, TagWhitelist.FORMATTING, true), SanitizedContent.ContentKind.HTML, contentDir);
    }

    public static String escapeHtmlRcdata(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.HTML)) {
            return Sanitizers.normalizeHtml(value.coerceToString());
        }
        return Sanitizers.escapeHtml(value.coerceToString());
    }

    public static String escapeHtmlRcdata(String value) {
        return EscapingConventions.EscapeHtml.INSTANCE.escape(value);
    }

    public static String normalizeHtml(SoyValue value) {
        return Sanitizers.normalizeHtml(value.coerceToString());
    }

    public static String normalizeHtml(String value) {
        return EscapingConventions.NormalizeHtml.INSTANCE.escape(value);
    }

    public static String normalizeHtmlNospace(SoyValue value) {
        return Sanitizers.normalizeHtmlNospace(value.coerceToString());
    }

    public static String normalizeHtmlNospace(String value) {
        return EscapingConventions.NormalizeHtmlNospace.INSTANCE.escape(value);
    }

    public static String escapeHtmlAttribute(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.HTML)) {
            return Sanitizers.stripHtmlTags(value.coerceToString(), null, true);
        }
        return Sanitizers.escapeHtmlAttribute(value.coerceToString());
    }

    public static String escapeHtmlAttribute(String value) {
        return EscapingConventions.EscapeHtml.INSTANCE.escape(value);
    }

    public static String escapeHtmlAttributeNospace(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.HTML)) {
            return Sanitizers.stripHtmlTags(value.coerceToString(), null, false);
        }
        return Sanitizers.escapeHtmlAttributeNospace(value.coerceToString());
    }

    public static String escapeHtmlAttributeNospace(String value) {
        return EscapingConventions.EscapeHtmlNospace.INSTANCE.escape(value);
    }

    public static String escapeJsString(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.JS_STR_CHARS)) {
            return value.coerceToString();
        }
        return Sanitizers.escapeJsString(value.coerceToString());
    }

    public static String escapeJsString(String value) {
        return EscapingConventions.EscapeJsString.INSTANCE.escape(value);
    }

    public static String escapeJsValue(SoyValue value) {
        if (NullData.INSTANCE == value) {
            return " null ";
        }
        if (value instanceof NumberData) {
            return " " + value.numberValue() + " ";
        }
        if (value instanceof BooleanData) {
            return " " + value.booleanValue() + " ";
        }
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.JS)) {
            return value.coerceToString();
        }
        return Sanitizers.escapeJsValue(value.coerceToString());
    }

    public static String escapeJsValue(String value) {
        return value != null ? "'" + Sanitizers.escapeJsString(value) + "'" : " null ";
    }

    public static String escapeJsRegex(SoyValue value) {
        return Sanitizers.escapeJsRegex(value.coerceToString());
    }

    public static String escapeJsRegex(String value) {
        return EscapingConventions.EscapeJsRegex.INSTANCE.escape(value);
    }

    public static String escapeCssString(SoyValue value) {
        return Sanitizers.escapeCssString(value.coerceToString());
    }

    public static String escapeCssString(String value) {
        return EscapingConventions.EscapeCssString.INSTANCE.escape(value);
    }

    public static String filterCssValue(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.CSS)) {
            return value.coerceToString();
        }
        return NullData.INSTANCE == value ? "" : Sanitizers.filterCssValue(value.coerceToString());
    }

    public static String filterCssValue(String value) {
        if (EscapingConventions.FilterCssValue.INSTANCE.getValueFilter().matcher(value).find()) {
            return value;
        }
        LOGGER.log(Level.WARNING, "|filterCssValue received bad value {0}", value);
        return EscapingConventions.FilterCssValue.INSTANCE.getInnocuousOutput();
    }

    public static String escapeUri(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.URI)) {
            return Sanitizers.normalizeUri(value);
        }
        return Sanitizers.escapeUri(value.coerceToString());
    }

    public static String escapeUri(String value) {
        return CharEscapers.uriEscaper(false).escape(value);
    }

    public static String normalizeUri(SoyValue value) {
        return Sanitizers.normalizeUri(value.coerceToString());
    }

    public static String normalizeUri(String value) {
        return EscapingConventions.NormalizeUri.INSTANCE.escape(value);
    }

    public static String filterNormalizeUri(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.URI)) {
            return Sanitizers.normalizeUri(value);
        }
        return Sanitizers.filterNormalizeUri(value.coerceToString());
    }

    public static String filterNormalizeUri(String value) {
        if (EscapingConventions.FilterNormalizeUri.INSTANCE.getValueFilter().matcher(value).find()) {
            return EscapingConventions.FilterNormalizeUri.INSTANCE.escape(value);
        }
        LOGGER.log(Level.WARNING, "|filterNormalizeUri received bad value {0}", value);
        return EscapingConventions.FilterNormalizeUri.INSTANCE.getInnocuousOutput();
    }

    public static SanitizedContent filterImageDataUri(SoyValue value) {
        return Sanitizers.filterImageDataUri(value.coerceToString());
    }

    public static SanitizedContent filterImageDataUri(String value) {
        if (EscapingConventions.FilterImageDataUri.INSTANCE.getValueFilter().matcher(value).find()) {
            return UnsafeSanitizedContentOrdainer.ordainAsSafe(value, SanitizedContent.ContentKind.URI);
        }
        LOGGER.log(Level.WARNING, "|filterImageDataUri received bad value {0}", value);
        return UnsafeSanitizedContentOrdainer.ordainAsSafe(EscapingConventions.FilterImageDataUri.INSTANCE.getInnocuousOutput(), SanitizedContent.ContentKind.URI);
    }

    public static String filterHtmlAttributes(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.ATTRIBUTES)) {
            char lastChar;
            String content = value.coerceToString();
            if (content.length() > 0 && (lastChar = content.charAt(content.length() - 1)) != '\"' && lastChar != '\'' && !Character.isWhitespace(lastChar)) {
                content = content + ' ';
            }
            return content;
        }
        return Sanitizers.filterHtmlAttributes(value.coerceToString());
    }

    public static String filterHtmlAttributes(String value) {
        if (EscapingConventions.FilterHtmlAttributes.INSTANCE.getValueFilter().matcher(value).find()) {
            return value;
        }
        LOGGER.log(Level.WARNING, "|filterHtmlAttributes received bad value {0}", value);
        return EscapingConventions.FilterHtmlAttributes.INSTANCE.getInnocuousOutput();
    }

    public static String filterHtmlElementName(SoyValue value) {
        return Sanitizers.filterHtmlElementName(value.coerceToString());
    }

    public static String filterHtmlElementName(String value) {
        if (EscapingConventions.FilterHtmlElementName.INSTANCE.getValueFilter().matcher(value).find()) {
            return value;
        }
        LOGGER.log(Level.WARNING, "|filterHtmlElementName received bad value {0}", value);
        return EscapingConventions.FilterHtmlElementName.INSTANCE.getInnocuousOutput();
    }

    public static SoyValue filterNoAutoescape(SoyValue value) {
        if (Sanitizers.isSanitizedContentOfKind(value, SanitizedContent.ContentKind.TEXT)) {
            LOGGER.log(Level.WARNING, "|noAutoescape received value explicitly tagged as ContentKind.TEXT: {0}", value);
            return StringData.forValue("zSoyz");
        }
        return value;
    }

    private static boolean isSanitizedContentOfKind(SoyValue value, SanitizedContent.ContentKind kind) {
        return value instanceof SanitizedContent && kind == ((SanitizedContent)value).getContentKind();
    }

    @VisibleForTesting
    static String stripHtmlTags(String value, TagWhitelist safeTags, boolean rawSpacesAllowed) {
        EscapingConventions.CrossLanguageStringXform normalizer = rawSpacesAllowed ? EscapingConventions.NormalizeHtml.INSTANCE : EscapingConventions.NormalizeHtmlNospace.INSTANCE;
        Matcher matcher = EscapingConventions.HTML_TAG_CONTENT.matcher(value);
        if (!matcher.find()) {
            return normalizer.escape(value);
        }
        StringBuilder out = new StringBuilder(value.length() - matcher.end() + matcher.start());
        Appendable normalizedOut = normalizer.escape(out);
        List openTags = null;
        try {
            int pos = 0;
            do {
                String tagName;
                int start;
                if (pos < (start = matcher.start())) {
                    normalizedOut.append(value, pos, start);
                    if (value.charAt(start - 1) == '&') {
                        out.append("amp;");
                    }
                }
                if (safeTags != null && (tagName = matcher.group(1)) != null && safeTags.isSafeTag(tagName = tagName.toLowerCase(Locale.ENGLISH))) {
                    boolean isClose;
                    boolean bl = isClose = value.charAt(start + 1) == '/';
                    if (isClose) {
                        int lastIdx;
                        if (openTags != null && (lastIdx = openTags.lastIndexOf(tagName)) >= 0) {
                            Sanitizers.closeTags(openTags.subList(lastIdx, openTags.size()), out);
                        }
                    } else {
                        out.append('<').append(tagName).append('>');
                        if (!HTML5_VOID_ELEMENTS.contains(tagName)) {
                            if (openTags == null) {
                                openTags = Lists.newArrayList();
                            }
                            openTags.add(tagName);
                        }
                    }
                }
                pos = matcher.end();
            } while (matcher.find());
            normalizedOut.append(value, pos, value.length());
            if (openTags != null) {
                Sanitizers.closeTags(openTags, out);
            }
        }
        catch (IOException ex) {
            throw new AssertionError((Object)ex);
        }
        return out.toString();
    }

    private static void closeTags(List<String> openTags, StringBuilder out) {
        int i = openTags.size();
        while (--i >= 0) {
            out.append("</").append(openTags.get(i)).append('>');
        }
        openTags.clear();
    }
}

