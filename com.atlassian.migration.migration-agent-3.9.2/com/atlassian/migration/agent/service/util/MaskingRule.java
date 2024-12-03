/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.util;

import java.util.regex.Pattern;

class MaskingRule {
    private Pattern pattern;
    private String replacement;

    public MaskingRule() {
    }

    public MaskingRule(Pattern pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    public void setPattern(String pattern) {
        this.pattern = Pattern.compile(pattern, 2);
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public String getReplacement() {
        return this.replacement;
    }
}

