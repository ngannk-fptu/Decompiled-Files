/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.messages.Message;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;

public class QueryNodeParseException
extends QueryNodeException {
    private CharSequence query;
    private int beginColumn = -1;
    private int beginLine = -1;
    private String errorToken = "";

    public QueryNodeParseException(Message message) {
        super(message);
    }

    public QueryNodeParseException(Throwable throwable) {
        super(throwable);
    }

    public QueryNodeParseException(Message message, Throwable throwable) {
        super(message, throwable);
    }

    public void setQuery(CharSequence query) {
        this.query = query;
        this.message = new MessageImpl(QueryParserMessages.INVALID_SYNTAX_CANNOT_PARSE, query, "");
    }

    public CharSequence getQuery() {
        return this.query;
    }

    protected void setErrorToken(String errorToken) {
        this.errorToken = errorToken;
    }

    public String getErrorToken() {
        return this.errorToken;
    }

    public void setNonLocalizedMessage(Message message) {
        this.message = message;
    }

    public int getBeginLine() {
        return this.beginLine;
    }

    public int getBeginColumn() {
        return this.beginColumn;
    }

    protected void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    protected void setBeginColumn(int beginColumn) {
        this.beginColumn = beginColumn;
    }
}

