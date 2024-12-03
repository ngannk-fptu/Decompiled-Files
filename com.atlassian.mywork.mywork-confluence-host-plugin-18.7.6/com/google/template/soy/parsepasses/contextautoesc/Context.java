/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.parsepasses.contextautoesc.EscapingMode;
import java.util.List;
import javax.annotation.Nullable;

public final class Context {
    public final State state;
    public final ElementType elType;
    public final AttributeType attrType;
    public final AttributeEndDelimiter delimType;
    public final JsFollowingSlash slashType;
    public final UriPart uriPart;
    public static final Context HTML_PCDATA = new Context(State.HTML_PCDATA);
    public static final Context HTML_COMMENT = new Context(State.HTML_COMMENT);
    public static final Context HTML_BEFORE_TAG_NAME = new Context(State.HTML_BEFORE_TAG_NAME);
    public static final Context HTML_TAG_NAME = new Context(State.HTML_TAG_NAME);
    public static final Context CSS = new Context(State.CSS);
    public static final Context JS = new Context(State.JS).derive(JsFollowingSlash.REGEX);
    public static final Context TEXT = new Context(State.TEXT);
    public static final Context URI_START = new Context(State.URI).derive(UriPart.START);
    public static final Context ERROR = new Context(State.ERROR);
    private static final int N_STATE_BITS = 5;
    private static final int N_ELEMENT_BITS = 3;
    private static final int N_ATTR_BITS = 3;
    private static final int N_DELIM_BITS = 2;
    private static final int N_JS_SLASH_BITS = 2;
    private static final int N_URI_PART_BITS = 3;
    private static final ImmutableMap<SanitizedContent.ContentKind, Context> CONTENT_KIND_TO_START_CONTEXT_MAP;

    public Context(State state, ElementType elType, AttributeType attrType, AttributeEndDelimiter delimType, JsFollowingSlash slashType, UriPart uriPart) {
        this.state = state;
        this.elType = elType;
        this.attrType = attrType;
        this.delimType = delimType;
        this.slashType = slashType;
        this.uriPart = uriPart;
    }

    public Context(State state) {
        this(state, ElementType.NONE, AttributeType.NONE, AttributeEndDelimiter.NONE, JsFollowingSlash.NONE, UriPart.NONE);
    }

    public Context derive(State state) {
        return state == this.state ? this : new Context(state, this.elType, this.attrType, this.delimType, this.slashType, this.uriPart);
    }

    public Context derive(ElementType elType) {
        return elType == this.elType ? this : new Context(this.state, elType, this.attrType, this.delimType, this.slashType, this.uriPart);
    }

    public Context derive(AttributeType attrType) {
        return attrType == this.attrType ? this : new Context(this.state, this.elType, attrType, this.delimType, this.slashType, this.uriPart);
    }

    public Context derive(AttributeEndDelimiter delimType) {
        return delimType == this.delimType ? this : new Context(this.state, this.elType, this.attrType, delimType, this.slashType, this.uriPart);
    }

    public Context derive(JsFollowingSlash slashType) {
        return slashType == this.slashType ? this : new Context(this.state, this.elType, this.attrType, this.delimType, slashType, this.uriPart);
    }

    public Context derive(UriPart uriPart) {
        return uriPart == this.uriPart ? this : new Context(this.state, this.elType, this.attrType, this.delimType, this.slashType, uriPart);
    }

    public Context getContextAfterEscaping(@Nullable EscapingMode mode) {
        if (mode == null) {
            return ERROR;
        }
        if (mode == EscapingMode.ESCAPE_JS_VALUE) {
            switch (this.slashType) {
                case DIV_OP: 
                case UNKNOWN: {
                    return this;
                }
                case REGEX: {
                    return this.derive(JsFollowingSlash.DIV_OP);
                }
            }
            throw new IllegalStateException(this.slashType.name());
        }
        if (this.state == State.HTML_BEFORE_TAG_NAME) {
            return HTML_TAG_NAME;
        }
        if (this.state == State.HTML_TAG) {
            return new Context(State.HTML_ATTRIBUTE_NAME, this.elType, AttributeType.PLAIN_TEXT, this.delimType, this.slashType, this.uriPart);
        }
        if (this.uriPart == UriPart.START) {
            return this.derive(UriPart.PRE_QUERY);
        }
        return this;
    }

    Context getContextBeforeDynamicValue() {
        if (this.state == State.HTML_BEFORE_ATTRIBUTE_VALUE) {
            return Context.computeContextAfterAttributeDelimiter(this.elType, this.attrType, AttributeEndDelimiter.SPACE_OR_TAG_END);
        }
        return this;
    }

    static Context computeContextAfterAttributeDelimiter(ElementType elType, AttributeType attrType, AttributeEndDelimiter delim) {
        State state;
        JsFollowingSlash slash = JsFollowingSlash.NONE;
        UriPart uriPart = UriPart.NONE;
        switch (attrType) {
            case PLAIN_TEXT: {
                state = State.HTML_NORMAL_ATTR_VALUE;
                break;
            }
            case SCRIPT: {
                state = State.JS;
                slash = JsFollowingSlash.REGEX;
                break;
            }
            case STYLE: {
                state = State.CSS;
                break;
            }
            case URI: {
                state = State.URI;
                uriPart = UriPart.START;
                break;
            }
            default: {
                throw new AssertionError((Object)("Unexpected attribute type " + (Object)((Object)attrType)));
            }
        }
        return new Context(state, elType, attrType, delim, slash, uriPart);
    }

    @Nullable
    public List<EscapingMode> getEscapingModes() {
        EscapingMode escapingMode = this.state.escapingMode;
        if (escapingMode == null) {
            return ImmutableList.of();
        }
        EscapingMode extraEscapingMode = null;
        switch (this.uriPart) {
            case QUERY: {
                escapingMode = EscapingMode.ESCAPE_URI;
                break;
            }
            case START: {
                if (escapingMode != EscapingMode.NORMALIZE_URI) {
                    extraEscapingMode = escapingMode;
                }
                escapingMode = EscapingMode.FILTER_NORMALIZE_URI;
                break;
            }
            case UNKNOWN: 
            case UNKNOWN_PRE_FRAGMENT: {
                return ImmutableList.of();
            }
        }
        switch (this.delimType) {
            case SPACE_OR_TAG_END: {
                if (escapingMode == EscapingMode.ESCAPE_HTML_ATTRIBUTE || escapingMode == EscapingMode.NORMALIZE_URI) {
                    escapingMode = EscapingMode.ESCAPE_HTML_ATTRIBUTE_NOSPACE;
                    break;
                }
                extraEscapingMode = EscapingMode.ESCAPE_HTML_ATTRIBUTE_NOSPACE;
                break;
            }
            case SINGLE_QUOTE: 
            case DOUBLE_QUOTE: {
                if (escapingMode == EscapingMode.NORMALIZE_URI) {
                    escapingMode = EscapingMode.ESCAPE_HTML_ATTRIBUTE;
                    break;
                }
                if (escapingMode.isHtmlEmbeddable) break;
                extraEscapingMode = EscapingMode.ESCAPE_HTML_ATTRIBUTE;
                break;
            }
        }
        return extraEscapingMode == null ? ImmutableList.of((Object)((Object)escapingMode)) : ImmutableList.of((Object)((Object)escapingMode), (Object)((Object)extraEscapingMode));
    }

    public boolean isCompatibleWith(EscapingMode mode) {
        if (mode == EscapingMode.ESCAPE_JS_VALUE) {
            switch (this.state) {
                case JS_SQ_STRING: 
                case JS_DQ_STRING: 
                case CSS_SQ_STRING: 
                case CSS_DQ_STRING: {
                    return false;
                }
            }
            return true;
        }
        if (mode == EscapingMode.TEXT) {
            return this.equals(TEXT);
        }
        return this.delimType != AttributeEndDelimiter.SPACE_OR_TAG_END || mode != EscapingMode.ESCAPE_HTML && mode != EscapingMode.ESCAPE_HTML_ATTRIBUTE && mode != EscapingMode.ESCAPE_HTML_RCDATA;
    }

    public boolean isErrorContext() {
        return this.state == State.ERROR;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Context)) {
            return false;
        }
        Context that = (Context)o;
        return this.state == that.state && this.elType == that.elType && this.attrType == that.attrType && this.delimType == that.delimType && this.slashType == that.slashType && this.uriPart == that.uriPart;
    }

    public int hashCode() {
        return this.packedBits();
    }

    public int packedBits() {
        return (((this.uriPart.ordinal() << 2 | this.slashType.ordinal() << 2 | this.delimType.ordinal()) << 3 | this.attrType.ordinal()) << 3 | this.elType.ordinal()) << 5 | this.state.ordinal();
    }

    public static Context union(Context a, Context b) {
        if (a.equals(b)) {
            return a;
        }
        if (a.equals(b.derive(a.slashType))) {
            return a.derive(JsFollowingSlash.UNKNOWN);
        }
        if (a.equals(b.derive(a.uriPart))) {
            return a.derive(a.uriPart != UriPart.FRAGMENT && b.uriPart != UriPart.FRAGMENT && a.uriPart != UriPart.UNKNOWN && b.uriPart != UriPart.UNKNOWN ? UriPart.UNKNOWN_PRE_FRAGMENT : UriPart.UNKNOWN);
        }
        if (a.state.compareTo(b.state) > 0) {
            Context swap = a;
            a = b;
            b = swap;
        }
        if (a.state == State.HTML_TAG_NAME && b.state == State.HTML_TAG) {
            return b;
        }
        if (a.state == State.HTML_TAG && a.elType == b.elType && (b.state == State.HTML_ATTRIBUTE_NAME || b.delimType == AttributeEndDelimiter.SPACE_OR_TAG_END)) {
            return a;
        }
        return ERROR;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("(Context ").append(this.state.name());
        if (this.elType != ElementType.NONE) {
            sb.append(' ').append(this.elType.name());
        }
        if (this.attrType != AttributeType.NONE) {
            sb.append(' ').append(this.attrType.name());
        }
        if (this.delimType != AttributeEndDelimiter.NONE) {
            sb.append(' ').append(this.delimType.name());
        }
        if (this.slashType != JsFollowingSlash.NONE) {
            sb.append(' ').append(this.slashType.name());
        }
        if (this.uriPart != UriPart.NONE) {
            sb.append(' ').append(this.uriPart.name());
        }
        return sb.append(')').toString();
    }

    public static Context getStartContextForContentKind(SanitizedContent.ContentKind contentKind) {
        return (Context)Preconditions.checkNotNull((Object)CONTENT_KIND_TO_START_CONTEXT_MAP.get((Object)contentKind));
    }

    public static boolean isValidStartContextForContentKind(SanitizedContent.ContentKind contentKind, Context context) {
        switch (contentKind) {
            case ATTRIBUTES: {
                return context.state == State.HTML_ATTRIBUTE_NAME || context.state == State.HTML_TAG;
            }
        }
        return context.equals(Context.getStartContextForContentKind(contentKind));
    }

    public static boolean isValidStartContextForContentKindLoose(SanitizedContent.ContentKind contentKind, Context context) {
        switch (contentKind) {
            case URI: {
                return context.state == State.URI;
            }
        }
        return Context.isValidStartContextForContentKind(contentKind, context);
    }

    public SanitizedContent.ContentKind getMostAppropriateContentKind() {
        for (SanitizedContent.ContentKind contentKind : CONTENT_KIND_TO_START_CONTEXT_MAP.keySet()) {
            if (!Context.isValidStartContextForContentKindLoose(contentKind, this)) continue;
            return contentKind;
        }
        return SanitizedContent.ContentKind.TEXT;
    }

    public static boolean isValidEndContextForContentKind(SanitizedContent.ContentKind contentKind, Context context) {
        switch (contentKind) {
            case CSS: {
                return context.equals(CSS);
            }
            case HTML: {
                return context.equals(HTML_PCDATA);
            }
            case ATTRIBUTES: {
                return context.state == State.HTML_ATTRIBUTE_NAME || context.state == State.HTML_TAG;
            }
            case JS: {
                return context.state == State.JS;
            }
            case URI: {
                return context.state == State.URI && context.uriPart != UriPart.START;
            }
            case TEXT: {
                return context.equals(TEXT);
            }
        }
        throw new IllegalArgumentException("Specified content kind has no associated end context.");
    }

    public static String getLikelyEndContextMismatchCause(SanitizedContent.ContentKind contentKind, Context context) {
        Preconditions.checkArgument((!Context.isValidEndContextForContentKind(contentKind, context) ? 1 : 0) != 0);
        if (contentKind == SanitizedContent.ContentKind.ATTRIBUTES) {
            return "an unterminated attribute value, or ending with an unquoted attribute";
        }
        switch (context.state) {
            case HTML_TAG_NAME: 
            case HTML_TAG: 
            case HTML_ATTRIBUTE_NAME: 
            case HTML_NORMAL_ATTR_VALUE: {
                return "an unterminated HTML tag or attribute";
            }
            case CSS: {
                return "an unclosed style block or attribute";
            }
            case JS: {
                return "an unclosed script block or attribute";
            }
            case CSS_COMMENT: 
            case HTML_COMMENT: 
            case JS_LINE_COMMENT: 
            case JS_BLOCK_COMMENT: {
                return "an unterminated comment";
            }
            case JS_SQ_STRING: 
            case JS_DQ_STRING: 
            case CSS_SQ_STRING: 
            case CSS_DQ_STRING: {
                return "an unterminated string literal";
            }
            case URI: 
            case CSS_URI: 
            case CSS_DQ_URI: 
            case CSS_SQ_URI: {
                return "an unterminated or empty URI";
            }
            case JS_REGEX: {
                return "an unterminated regular expression";
            }
        }
        return "unknown to compiler";
    }

    static {
        if (32 < State.values().length || 8 < ElementType.values().length || 8 < AttributeType.values().length || 4 < AttributeEndDelimiter.values().length || 4 < JsFollowingSlash.values().length || 8 < UriPart.values().length) {
            throw new AssertionError();
        }
        CONTENT_KIND_TO_START_CONTEXT_MAP = ImmutableMap.builder().put((Object)SanitizedContent.ContentKind.CSS, (Object)CSS).put((Object)SanitizedContent.ContentKind.HTML, (Object)HTML_PCDATA).put((Object)SanitizedContent.ContentKind.ATTRIBUTES, (Object)new Context(State.HTML_TAG)).put((Object)SanitizedContent.ContentKind.JS, (Object)JS).put((Object)SanitizedContent.ContentKind.URI, (Object)URI_START).put((Object)SanitizedContent.ContentKind.TEXT, (Object)TEXT).build();
    }

    public static enum UriPart {
        NONE,
        START,
        PRE_QUERY,
        QUERY,
        FRAGMENT,
        UNKNOWN_PRE_FRAGMENT,
        UNKNOWN;

    }

    public static enum JsFollowingSlash {
        NONE,
        REGEX,
        DIV_OP,
        UNKNOWN;

    }

    public static enum AttributeEndDelimiter {
        NONE,
        DOUBLE_QUOTE("\""),
        SINGLE_QUOTE("'"),
        SPACE_OR_TAG_END("");

        @Nullable
        public final String text;

        private AttributeEndDelimiter(String text) {
            this.text = text;
        }

        private AttributeEndDelimiter() {
            this.text = null;
        }
    }

    public static enum AttributeType {
        NONE,
        SCRIPT,
        STYLE,
        URI,
        PLAIN_TEXT;

    }

    public static enum ElementType {
        NONE,
        SCRIPT,
        STYLE,
        TEXTAREA,
        TITLE,
        LISTING,
        XMP,
        NORMAL;

    }

    public static enum State {
        HTML_PCDATA(EscapingMode.ESCAPE_HTML),
        HTML_RCDATA(EscapingMode.ESCAPE_HTML_RCDATA),
        HTML_BEFORE_TAG_NAME(EscapingMode.FILTER_HTML_ELEMENT_NAME),
        HTML_TAG_NAME(EscapingMode.FILTER_HTML_ELEMENT_NAME),
        HTML_TAG(EscapingMode.FILTER_HTML_ATTRIBUTES),
        HTML_ATTRIBUTE_NAME(EscapingMode.FILTER_HTML_ATTRIBUTES),
        HTML_BEFORE_ATTRIBUTE_VALUE,
        HTML_COMMENT(EscapingMode.ESCAPE_HTML_RCDATA),
        HTML_NORMAL_ATTR_VALUE(EscapingMode.ESCAPE_HTML_ATTRIBUTE),
        CSS(EscapingMode.FILTER_CSS_VALUE),
        CSS_COMMENT,
        CSS_DQ_STRING(EscapingMode.ESCAPE_CSS_STRING),
        CSS_SQ_STRING(EscapingMode.ESCAPE_CSS_STRING),
        CSS_URI(EscapingMode.NORMALIZE_URI),
        CSS_DQ_URI(EscapingMode.NORMALIZE_URI),
        CSS_SQ_URI(EscapingMode.NORMALIZE_URI),
        JS(EscapingMode.ESCAPE_JS_VALUE),
        JS_LINE_COMMENT,
        JS_BLOCK_COMMENT,
        JS_DQ_STRING(EscapingMode.ESCAPE_JS_STRING),
        JS_SQ_STRING(EscapingMode.ESCAPE_JS_STRING),
        JS_REGEX(EscapingMode.ESCAPE_JS_REGEX),
        URI(EscapingMode.NORMALIZE_URI),
        TEXT(EscapingMode.TEXT),
        ERROR;

        @Nullable
        private final EscapingMode escapingMode;

        private State(EscapingMode escapingMode) {
            this.escapingMode = escapingMode;
        }

        private State() {
            this.escapingMode = null;
        }
    }
}

