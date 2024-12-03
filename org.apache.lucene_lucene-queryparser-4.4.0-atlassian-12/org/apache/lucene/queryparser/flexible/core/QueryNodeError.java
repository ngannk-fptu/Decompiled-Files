/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core;

import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.NLSException;

public class QueryNodeError
extends Error
implements NLSException {
    private Message message;

    public QueryNodeError(Message message) {
        super(message.getKey());
        this.message = message;
    }

    public QueryNodeError(Throwable throwable) {
        super(throwable);
    }

    public QueryNodeError(Message message, Throwable throwable) {
        super(message.getKey(), throwable);
        this.message = message;
    }

    @Override
    public Message getMessageObject() {
        return this.message;
    }
}

