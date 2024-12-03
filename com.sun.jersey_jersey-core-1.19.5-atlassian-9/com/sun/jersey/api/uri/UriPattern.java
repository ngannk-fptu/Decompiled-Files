/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.uri;

import com.sun.jersey.api.uri.UriTemplateParser;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UriPattern {
    public static final UriPattern EMPTY = new UriPattern();
    private final String regex;
    private final Pattern regexPattern;
    private final int[] groupIndexes;
    private static final EmptyStringMatchResult EMPTY_STRING_MATCH_RESULT = new EmptyStringMatchResult();

    protected UriPattern() {
        this.regex = "";
        this.regexPattern = null;
        this.groupIndexes = null;
    }

    public UriPattern(String regex) {
        this(regex, UriTemplateParser.EMPTY_INT_ARRAY);
    }

    public UriPattern(String regex, int[] groupIndexes) {
        this(UriPattern.compile(regex), groupIndexes);
    }

    private static Pattern compile(String regex) {
        return regex == null || regex.length() == 0 ? null : Pattern.compile(regex);
    }

    public UriPattern(Pattern regexPattern) {
        this(regexPattern, UriTemplateParser.EMPTY_INT_ARRAY);
    }

    public UriPattern(Pattern regexPattern, int[] groupIndexes) {
        if (regexPattern == null) {
            throw new IllegalArgumentException();
        }
        this.regex = regexPattern.toString();
        this.regexPattern = regexPattern;
        this.groupIndexes = groupIndexes;
    }

    public final String getRegex() {
        return this.regex;
    }

    public final int[] getGroupIndexes() {
        return this.groupIndexes;
    }

    public final MatchResult match(CharSequence uri) {
        if (uri == null || uri.length() == 0) {
            return this.regexPattern == null ? EMPTY_STRING_MATCH_RESULT : null;
        }
        if (this.regexPattern == null) {
            return null;
        }
        Matcher m = this.regexPattern.matcher(uri);
        if (!m.matches()) {
            return null;
        }
        return this.groupIndexes.length > 0 ? new GroupIndexMatchResult(m) : m;
    }

    public final boolean match(CharSequence uri, List<String> groupValues) {
        if (groupValues == null) {
            throw new IllegalArgumentException();
        }
        if (uri == null || uri.length() == 0) {
            return this.regexPattern == null;
        }
        if (this.regexPattern == null) {
            return false;
        }
        Matcher m = this.regexPattern.matcher(uri);
        if (!m.matches()) {
            return false;
        }
        groupValues.clear();
        if (this.groupIndexes.length > 0) {
            for (int i = 0; i < this.groupIndexes.length - 1; ++i) {
                groupValues.add(m.group(this.groupIndexes[i]));
            }
        } else {
            for (int i = 1; i <= m.groupCount(); ++i) {
                groupValues.add(m.group(i));
            }
        }
        return true;
    }

    public final boolean match(CharSequence uri, List<String> groupNames, Map<String, String> groupValues) {
        if (groupValues == null) {
            throw new IllegalArgumentException();
        }
        if (uri == null || uri.length() == 0) {
            return this.regexPattern == null;
        }
        if (this.regexPattern == null) {
            return false;
        }
        Matcher m = this.regexPattern.matcher(uri);
        if (!m.matches()) {
            return false;
        }
        groupValues.clear();
        for (int i = 0; i < groupNames.size(); ++i) {
            String name = groupNames.get(i);
            String currentValue = m.group(this.groupIndexes.length > 0 ? this.groupIndexes[i] : i + 1);
            String previousValue = groupValues.get(name);
            if (previousValue != null && !previousValue.equals(currentValue)) {
                return false;
            }
            groupValues.put(name, currentValue);
        }
        return true;
    }

    public final int hashCode() {
        return this.regex.hashCode();
    }

    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        UriPattern that = (UriPattern)obj;
        return this.regex == that.regex || this.regex != null && this.regex.equals(that.regex);
    }

    public final String toString() {
        return this.regex;
    }

    private final class GroupIndexMatchResult
    implements MatchResult {
        private final MatchResult r;

        GroupIndexMatchResult(MatchResult r) {
            this.r = r;
        }

        @Override
        public int start() {
            return this.r.start();
        }

        @Override
        public int start(int group) {
            if (group > this.groupCount()) {
                throw new IndexOutOfBoundsException();
            }
            return group > 0 ? this.r.start(UriPattern.this.groupIndexes[group - 1]) : this.r.start();
        }

        @Override
        public int end() {
            return this.r.end();
        }

        @Override
        public int end(int group) {
            if (group > this.groupCount()) {
                throw new IndexOutOfBoundsException();
            }
            return group > 0 ? this.r.end(UriPattern.this.groupIndexes[group - 1]) : this.r.end();
        }

        @Override
        public String group() {
            return this.r.group();
        }

        @Override
        public String group(int group) {
            if (group > this.groupCount()) {
                throw new IndexOutOfBoundsException();
            }
            return group > 0 ? this.r.group(UriPattern.this.groupIndexes[group - 1]) : this.r.group();
        }

        @Override
        public int groupCount() {
            return UriPattern.this.groupIndexes.length - 1;
        }
    }

    private static final class EmptyStringMatchResult
    implements MatchResult {
        private EmptyStringMatchResult() {
        }

        @Override
        public int start() {
            return 0;
        }

        @Override
        public int start(int group) {
            if (group != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this.start();
        }

        @Override
        public int end() {
            return 0;
        }

        @Override
        public int end(int group) {
            if (group != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this.end();
        }

        @Override
        public String group() {
            return "";
        }

        @Override
        public String group(int group) {
            if (group != 0) {
                throw new IndexOutOfBoundsException();
            }
            return this.group();
        }

        @Override
        public int groupCount() {
            return 0;
        }
    }
}

