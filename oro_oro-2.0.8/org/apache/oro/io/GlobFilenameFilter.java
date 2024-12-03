/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.io;

import org.apache.oro.io.RegexFilenameFilter;
import org.apache.oro.text.GlobCompiler;
import org.apache.oro.text.PatternCache;
import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Perl5Matcher;

public class GlobFilenameFilter
extends RegexFilenameFilter {
    private static final PatternMatcher __MATCHER = new Perl5Matcher();
    private static final PatternCache __CACHE = new PatternCacheLRU(new GlobCompiler());

    public GlobFilenameFilter(String string, int n) {
        super(__CACHE, __MATCHER, string, n);
    }

    public GlobFilenameFilter(String string) {
        super(__CACHE, __MATCHER, string);
    }

    public GlobFilenameFilter() {
        super(__CACHE, __MATCHER);
    }
}

