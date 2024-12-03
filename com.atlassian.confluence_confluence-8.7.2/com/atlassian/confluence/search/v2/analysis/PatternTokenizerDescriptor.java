/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.TokenizerDescriptor;
import java.util.regex.Pattern;

public class PatternTokenizerDescriptor
implements TokenizerDescriptor {
    private final Pattern pattern;
    private final int group;

    public PatternTokenizerDescriptor(Pattern pattern, int group) {
        this.pattern = pattern;
        this.group = group;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public int getGroup() {
        return this.group;
    }
}

