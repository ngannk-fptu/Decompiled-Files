/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.serialization.impl;

import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;

final class PortablePathCursor {
    private String path;
    private int index;
    private int offset;
    private int nextSplit;
    private String token;

    PortablePathCursor() {
    }

    void init(String path) {
        this.path = Preconditions.checkHasText(path, "path cannot be null or empty");
        this.index = 0;
        this.offset = 0;
        this.nextSplit = StringUtil.indexOf(path, '.', 0);
        this.token = null;
        if (this.nextSplit == 0) {
            throw new IllegalArgumentException("The path cannot begin with a dot: " + path);
        }
    }

    void initWithSingleTokenPath(String path) {
        this.path = path;
        this.index = 0;
        this.offset = 0;
        this.nextSplit = -1;
        this.token = path;
    }

    void reset() {
        this.path = null;
        this.index = -1;
        this.offset = 0;
        this.token = null;
    }

    boolean isLastToken() {
        return this.nextSplit == -1;
    }

    String token() {
        int endIndex;
        if (this.token != null) {
            return this.token;
        }
        int n = endIndex = this.nextSplit < 0 ? this.path.length() : this.nextSplit;
        if (endIndex <= this.offset) {
            throw new IllegalArgumentException("The token's length cannot be zero: " + this.path);
        }
        this.token = Preconditions.checkHasText(this.path.substring(this.offset, endIndex), "Token cannot be null or empty");
        return this.token;
    }

    String path() {
        return this.path;
    }

    boolean advanceToNextToken() {
        if (this.nextSplit == -1) {
            return false;
        }
        this.token = null;
        int oldNextSplit = this.nextSplit;
        this.nextSplit = StringUtil.indexOf(this.path, '.', oldNextSplit + 1);
        this.offset = oldNextSplit + 1;
        ++this.index;
        return true;
    }

    void index(int indexToNavigateTo) {
        this.index = 0;
        this.offset = 0;
        this.nextSplit = StringUtil.indexOf(this.path, '.', 0);
        this.token = null;
        for (int i = 1; i <= indexToNavigateTo; ++i) {
            if (this.advanceToNextToken()) continue;
            throw new IndexOutOfBoundsException("Index out of bound " + indexToNavigateTo + " in " + this.path);
        }
    }

    int index() {
        return this.index;
    }

    boolean isAnyPath() {
        return this.path.contains("[any]");
    }
}

