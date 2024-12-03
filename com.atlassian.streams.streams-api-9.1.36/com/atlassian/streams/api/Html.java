/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.api;

import com.atlassian.streams.api.common.Functions;
import com.atlassian.streams.api.common.Option;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Html
implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(Html.class);
    private final String value;

    public Html(String value) {
        this.value = this.stripControlChars((String)Preconditions.checkNotNull((Object)value));
    }

    @Deprecated
    public static Function<String, Html> html() {
        return HtmlF.INSTANCE;
    }

    @Deprecated
    public static Html html(String s) {
        return (Html)Html.html().apply((Object)s);
    }

    @Deprecated
    public static Function<Html, String> htmlToString() {
        return HtmlToString.INSTANCE;
    }

    @Deprecated
    public static Function<Html, Option<Html>> trimHtmlToNone() {
        return TrimHtmlToNone.INSTANCE;
    }

    public static Option<Html> trimHtml2None(Html h) {
        return ((Option)Functions.trimToNone().apply((Object)h.toString())).map(Html.html());
    }

    public String toString() {
        return this.value;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Html other = (Html)obj;
        return this.value.equals(other.value);
    }

    private String stripControlChars(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (this.illegal(c)) {
                log.debug("Invalid character encountered: codePoint = {}", (Object)String.valueOf(c).codePointAt(0));
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private boolean illegal(char c) {
        return this.between(c, '\u0000', '\b') || this.between(c, '\u000b', '\u001f') || this.between(c, '\ufffe', '\uffff');
    }

    private boolean between(char c, char minInclusive, char maxInclusive) {
        return c >= minInclusive && c <= maxInclusive;
    }

    @Deprecated
    private static enum TrimHtmlToNone implements Function<Html, Option<Html>>
    {
        INSTANCE;


        public Option<Html> apply(Html h) {
            return ((Option)Functions.trimToNone().apply((Object)h.toString())).map(Html.html());
        }
    }

    @Deprecated
    private static enum HtmlToString implements Function<Html, String>
    {
        INSTANCE;


        public String apply(Html h) {
            return h.toString();
        }
    }

    @Deprecated
    private static enum HtmlF implements Function<String, Html>
    {
        INSTANCE;


        public Html apply(String s) {
            return new Html(s);
        }
    }
}

