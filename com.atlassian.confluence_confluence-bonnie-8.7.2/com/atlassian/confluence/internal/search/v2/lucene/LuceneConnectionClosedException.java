/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;

public class LuceneConnectionClosedException
extends LuceneException {
    public LuceneConnectionClosedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LuceneConnectionClosedException(String message) {
        super(message);
    }

    public LuceneConnectionClosedException(Throwable cause) {
        super(cause);
    }
}

