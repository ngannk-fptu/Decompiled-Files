/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.osgi.hook.dmz;

public class PackageMatcher {
    private static final String WILDCARD_PATTERN = "*";
    private final String[] pattern;
    private int currentPatternIndex = 0;
    private final String[] input;
    private int currentInputIndex = 0;
    private boolean isWildcardMode = false;

    public PackageMatcher(String pattern, String input) {
        this.pattern = pattern.split("\\.");
        this.input = input.split("\\.");
    }

    public boolean match() {
        do {
            boolean localSuccess;
            if (this.pattern[this.currentPatternIndex].equals(WILDCARD_PATTERN)) {
                this.isWildcardMode = true;
            }
            boolean bl = localSuccess = this.isWildcardMode ? this.followsWildcardPattern() : this.followsRegularPattern();
            if (localSuccess) continue;
            return false;
        } while (this.currentPatternIndex < this.pattern.length);
        return this.currentInputIndex == this.input.length;
    }

    private boolean followsRegularPattern() {
        if (this.currentInputIndex == this.input.length) {
            return false;
        }
        String patternPackage = this.pattern[this.currentPatternIndex++];
        String inputPackage = this.input[this.currentInputIndex++];
        return patternPackage.equals(inputPackage);
    }

    private boolean followsWildcardPattern() {
        this.isWildcardMode = false;
        if (++this.currentPatternIndex == this.pattern.length) {
            this.currentInputIndex = this.input.length;
            return true;
        }
        String nextPatternToFind = this.pattern[this.currentPatternIndex++];
        boolean success = false;
        do {
            String nextInputPackage;
            if (!nextPatternToFind.equals(nextInputPackage = this.input[this.currentInputIndex++])) continue;
            success = true;
        } while (!success && this.currentInputIndex < this.input.length);
        return success;
    }
}

