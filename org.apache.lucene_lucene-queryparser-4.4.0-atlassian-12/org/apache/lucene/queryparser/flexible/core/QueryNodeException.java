/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core;

import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.messages.NLSException;

public class QueryNodeException
extends Exception
implements NLSException {
    protected Message message = new MessageImpl(QueryParserMessages.EMPTY_MESSAGE);

    public QueryNodeException(Message message) {
        super(message.getKey());
        this.message = message;
    }

    public QueryNodeException(Throwable throwable) {
        super(throwable);
    }

    public QueryNodeException(Message message, Throwable throwable) {
        super(message.getKey(), throwable);
        this.message = message;
    }

    @Override
    public Message getMessageObject() {
        return this.message;
    }

    @Override
    public String getMessage() {
        return this.getLocalizedMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return this.getLocalizedMessage(Locale.getDefault());
    }

    public String getLocalizedMessage(Locale locale) {
        return this.message.getLocalizedMessage(locale);
    }

    @Override
    public String toString() {
        return this.message.getKey() + ": " + this.getLocalizedMessage();
    }
}

