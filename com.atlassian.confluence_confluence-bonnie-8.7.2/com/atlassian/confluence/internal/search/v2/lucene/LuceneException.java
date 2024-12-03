/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

public class LuceneException
extends RuntimeException {
    public LuceneException(String message) {
        super(message);
    }

    public LuceneException(Throwable cause) {
        super(cause);
    }

    public LuceneException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

