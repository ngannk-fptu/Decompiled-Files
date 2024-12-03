/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.matcher;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.matcher.AbstractStringMatcher;
import org.apache.commons.text.matcher.StringMatcher;

public final class StringMatcherFactory {
    private static final AbstractStringMatcher.CharMatcher COMMA_MATCHER = new AbstractStringMatcher.CharMatcher(',');
    private static final AbstractStringMatcher.CharMatcher DOUBLE_QUOTE_MATCHER = new AbstractStringMatcher.CharMatcher('\"');
    public static final StringMatcherFactory INSTANCE = new StringMatcherFactory();
    private static final AbstractStringMatcher.NoneMatcher NONE_MATCHER = new AbstractStringMatcher.NoneMatcher();
    private static final AbstractStringMatcher.CharSetMatcher QUOTE_MATCHER = new AbstractStringMatcher.CharSetMatcher("'\"".toCharArray());
    private static final AbstractStringMatcher.CharMatcher SINGLE_QUOTE_MATCHER = new AbstractStringMatcher.CharMatcher('\'');
    private static final AbstractStringMatcher.CharMatcher SPACE_MATCHER = new AbstractStringMatcher.CharMatcher(' ');
    private static final AbstractStringMatcher.CharSetMatcher SPLIT_MATCHER = new AbstractStringMatcher.CharSetMatcher(" \t\n\r\f".toCharArray());
    private static final AbstractStringMatcher.CharMatcher TAB_MATCHER = new AbstractStringMatcher.CharMatcher('\t');
    private static final AbstractStringMatcher.TrimMatcher TRIM_MATCHER = new AbstractStringMatcher.TrimMatcher();

    private StringMatcherFactory() {
    }

    public StringMatcher andMatcher(StringMatcher ... stringMatchers) {
        int len = ArrayUtils.getLength((Object)stringMatchers);
        if (len == 0) {
            return NONE_MATCHER;
        }
        if (len == 1) {
            return stringMatchers[0];
        }
        return new AbstractStringMatcher.AndStringMatcher(stringMatchers);
    }

    public StringMatcher charMatcher(char ch) {
        return new AbstractStringMatcher.CharMatcher(ch);
    }

    public StringMatcher charSetMatcher(char ... chars) {
        int len = ArrayUtils.getLength((Object)chars);
        if (len == 0) {
            return NONE_MATCHER;
        }
        if (len == 1) {
            return new AbstractStringMatcher.CharMatcher(chars[0]);
        }
        return new AbstractStringMatcher.CharSetMatcher(chars);
    }

    public StringMatcher charSetMatcher(String chars) {
        int len = StringUtils.length((CharSequence)chars);
        if (len == 0) {
            return NONE_MATCHER;
        }
        if (len == 1) {
            return new AbstractStringMatcher.CharMatcher(chars.charAt(0));
        }
        return new AbstractStringMatcher.CharSetMatcher(chars.toCharArray());
    }

    public StringMatcher commaMatcher() {
        return COMMA_MATCHER;
    }

    public StringMatcher doubleQuoteMatcher() {
        return DOUBLE_QUOTE_MATCHER;
    }

    public StringMatcher noneMatcher() {
        return NONE_MATCHER;
    }

    public StringMatcher quoteMatcher() {
        return QUOTE_MATCHER;
    }

    public StringMatcher singleQuoteMatcher() {
        return SINGLE_QUOTE_MATCHER;
    }

    public StringMatcher spaceMatcher() {
        return SPACE_MATCHER;
    }

    public StringMatcher splitMatcher() {
        return SPLIT_MATCHER;
    }

    public StringMatcher stringMatcher(char ... chars) {
        int length = ArrayUtils.getLength((Object)chars);
        return length == 0 ? NONE_MATCHER : (length == 1 ? new AbstractStringMatcher.CharMatcher(chars[0]) : new AbstractStringMatcher.CharArrayMatcher(chars));
    }

    public StringMatcher stringMatcher(String str) {
        return StringUtils.isEmpty((CharSequence)str) ? NONE_MATCHER : this.stringMatcher(str.toCharArray());
    }

    public StringMatcher tabMatcher() {
        return TAB_MATCHER;
    }

    public StringMatcher trimMatcher() {
        return TRIM_MATCHER;
    }
}

