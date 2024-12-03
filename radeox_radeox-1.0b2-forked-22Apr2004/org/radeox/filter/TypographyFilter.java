/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import org.radeox.filter.CacheFilter;
import org.radeox.filter.regex.LocaleRegexReplaceFilter;

public class TypographyFilter
extends LocaleRegexReplaceFilter
implements CacheFilter {
    protected String getLocaleKey() {
        return "filter.typography";
    }
}

