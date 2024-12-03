/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.CharFilterDescriptor;
import java.util.regex.Pattern;

public class PatternReplaceCharFilterDescriptor
implements CharFilterDescriptor {
    private final Pattern pattern;
    private final String replacement;

    public PatternReplaceCharFilterDescriptor(Pattern pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public String getReplacement() {
        return this.replacement;
    }
}

