/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.template.soy.data.SanitizedContent;
import java.util.Map;
import javax.annotation.Nullable;

public enum EscapingMode {
    ESCAPE_HTML(true, SanitizedContent.ContentKind.HTML),
    ESCAPE_HTML_RCDATA(true, null),
    ESCAPE_HTML_ATTRIBUTE(true, null),
    ESCAPE_HTML_ATTRIBUTE_NOSPACE(true, null),
    FILTER_HTML_ELEMENT_NAME(true, null),
    FILTER_HTML_ATTRIBUTES(true, null),
    ESCAPE_JS_STRING(false, SanitizedContent.ContentKind.JS_STR_CHARS),
    ESCAPE_JS_VALUE(false, null),
    ESCAPE_JS_REGEX(false, null),
    ESCAPE_CSS_STRING(true, null),
    FILTER_CSS_VALUE(false, SanitizedContent.ContentKind.CSS),
    ESCAPE_URI(true, SanitizedContent.ContentKind.URI),
    NORMALIZE_URI(false, SanitizedContent.ContentKind.URI),
    FILTER_NORMALIZE_URI(false, SanitizedContent.ContentKind.URI),
    NO_AUTOESCAPE(false, SanitizedContent.ContentKind.TEXT),
    TEXT(false, SanitizedContent.ContentKind.TEXT, true);

    public final String directiveName = "|" + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    public final boolean isHtmlEmbeddable;
    @Nullable
    public final SanitizedContent.ContentKind contentKind;
    public final boolean isInternalOnly;
    private static final Map<String, EscapingMode> DIRECTIVE_TO_ESCAPING_MODE;

    private EscapingMode(boolean escapesQuotes, SanitizedContent.ContentKind contentKind, boolean internalOnly) {
        this.isHtmlEmbeddable = escapesQuotes;
        this.contentKind = contentKind;
        this.isInternalOnly = internalOnly;
    }

    private EscapingMode(boolean escapesQuotes, SanitizedContent.ContentKind contentKind) {
        this(escapesQuotes, contentKind, false);
    }

    @Nullable
    public static EscapingMode fromDirective(String directiveName) {
        return DIRECTIVE_TO_ESCAPING_MODE.get(directiveName);
    }

    static {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (EscapingMode mode : EscapingMode.values()) {
            builder.put((Object)mode.directiveName, (Object)mode);
        }
        DIRECTIVE_TO_ESCAPING_MODE = builder.build();
    }
}

