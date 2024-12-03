/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.TokenizerDescriptor;

public class PathHierarchyTokenizerDescriptor
implements TokenizerDescriptor {
    private final char delimiter;
    private final char replacement;
    private final int skip;

    public PathHierarchyTokenizerDescriptor() {
        this('/', '/', 0);
    }

    public PathHierarchyTokenizerDescriptor(char delimiter) {
        this(delimiter, delimiter, 0);
    }

    public PathHierarchyTokenizerDescriptor(char delimiter, char replacement, int skip) {
        this.delimiter = delimiter;
        this.replacement = replacement;
        this.skip = skip;
    }

    public char getDelimiter() {
        return this.delimiter;
    }

    public char getReplacement() {
        return this.replacement;
    }

    public int getSkip() {
        return this.skip;
    }
}

