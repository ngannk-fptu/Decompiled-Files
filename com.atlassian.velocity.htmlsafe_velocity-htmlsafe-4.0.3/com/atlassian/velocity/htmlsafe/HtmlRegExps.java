/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.velocity.htmlsafe;

import java.util.regex.Pattern;

public final class HtmlRegExps {
    public static final Pattern HTML_TAG_PATTERN = Pattern.compile("<([A-Z][A-Z0-9]*)\\b[^>]*>(?:.*?)</\\1>", 2);
    public static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&lt;(?:[A-Z][A-Z0-9]*)\\b(?:.(?!&gt;))*.?&gt;", 2);

    private HtmlRegExps() {
    }
}

