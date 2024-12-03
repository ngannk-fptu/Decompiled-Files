/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.security;

import java.util.Set;
import java.util.regex.Pattern;

public interface ExcludedPatternsChecker {
    public IsExcluded isExcluded(String var1);

    public void setExcludedPatterns(String var1);

    public void setExcludedPatterns(String[] var1);

    public void setExcludedPatterns(Set<String> var1);

    public Set<Pattern> getExcludedPatterns();

    public static final class IsExcluded {
        private final boolean excluded;
        private final String excludedPattern;

        public static IsExcluded yes(Pattern excludedPattern) {
            return new IsExcluded(true, excludedPattern.pattern());
        }

        public static IsExcluded no(Set<Pattern> excludedPatterns) {
            return new IsExcluded(false, excludedPatterns.toString());
        }

        private IsExcluded(boolean excluded, String excludedPattern) {
            this.excluded = excluded;
            this.excludedPattern = excludedPattern;
        }

        public boolean isExcluded() {
            return this.excluded;
        }

        public String getExcludedPattern() {
            return this.excludedPattern;
        }

        public String toString() {
            return "IsExcluded { excluded=" + this.excluded + ", excludedPattern=" + this.excludedPattern + " }";
        }
    }
}

