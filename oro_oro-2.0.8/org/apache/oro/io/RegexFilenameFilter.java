/*
 * Decompiled with CFR 0.152.
 */
package org.apache.oro.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import org.apache.oro.text.MalformedCachePatternException;
import org.apache.oro.text.PatternCache;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;

public abstract class RegexFilenameFilter
implements FilenameFilter,
FileFilter {
    PatternCache _cache;
    PatternMatcher _matcher;
    Pattern _pattern;

    RegexFilenameFilter(PatternCache patternCache, PatternMatcher patternMatcher, String string) {
        this._cache = patternCache;
        this._matcher = patternMatcher;
        this.setFilterExpression(string);
    }

    RegexFilenameFilter(PatternCache patternCache, PatternMatcher patternMatcher, String string, int n) {
        this._cache = patternCache;
        this._matcher = patternMatcher;
        this.setFilterExpression(string, n);
    }

    RegexFilenameFilter(PatternCache patternCache, PatternMatcher patternMatcher) {
        this(patternCache, patternMatcher, "");
    }

    public void setFilterExpression(String string) throws MalformedCachePatternException {
        this._pattern = this._cache.getPattern(string);
    }

    public void setFilterExpression(String string, int n) throws MalformedCachePatternException {
        this._pattern = this._cache.getPattern(string, n);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean accept(File file, String string) {
        PatternMatcher patternMatcher = this._matcher;
        synchronized (patternMatcher) {
            return this._matcher.matches(string, this._pattern);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean accept(File file) {
        PatternMatcher patternMatcher = this._matcher;
        synchronized (patternMatcher) {
            return this._matcher.matches(file.getName(), this._pattern);
        }
    }
}

