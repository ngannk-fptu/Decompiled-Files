/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.io;

import org.apache.oro.io.RegexFilenameFilter;
import org.apache.oro.text.PatternCache;
import org.apache.oro.text.PatternCacheLRU;
import org.apache.oro.text.awk.AwkCompiler;
import org.apache.oro.text.awk.AwkMatcher;
import org.apache.oro.text.regex.PatternMatcher;

public class AwkFilenameFilter
extends RegexFilenameFilter {
    private static final PatternMatcher __MATCHER = new AwkMatcher();
    private static final PatternCache __CACHE = new PatternCacheLRU(new AwkCompiler());

    public AwkFilenameFilter(String string, int n) {
        super(__CACHE, __MATCHER, string, n);
    }

    public AwkFilenameFilter(String string) {
        super(__CACHE, __MATCHER, string);
    }

    public AwkFilenameFilter() {
        super(__CACHE, __MATCHER);
    }
}

