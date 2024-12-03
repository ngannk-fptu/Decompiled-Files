/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.comment;

import org.supercsv.comment.CommentMatcher;

public class CommentStartsWith
implements CommentMatcher {
    private final String value;

    public CommentStartsWith(String value) {
        if (value == null) {
            throw new NullPointerException("value should not be null");
        }
        if (value.length() == 0) {
            throw new IllegalArgumentException("value should not be empty");
        }
        this.value = value;
    }

    public boolean isComment(String line) {
        return line.startsWith(this.value);
    }
}

