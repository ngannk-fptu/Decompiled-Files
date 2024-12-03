/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.api.factory.HttpClientOptions;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseParser;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.nio.NHttpMessageParser;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.reactor.SessionInputBuffer;

public class BoundedHttpResponseParserFactory
implements NHttpMessageParserFactory<HttpResponse> {
    private final HttpClientOptions httpClientOptions;

    public BoundedHttpResponseParserFactory(HttpClientOptions httpClientOptions) {
        this.httpClientOptions = httpClientOptions;
    }

    @Override
    public NHttpMessageParser<HttpResponse> create(SessionInputBuffer buffer, MessageConstraints constraints) {
        return new DefaultHttpResponseParser(buffer, (LineParser)BasicLineParser.INSTANCE, (HttpResponseFactory)DefaultHttpResponseFactory.INSTANCE, this.getConstraintsBuilder(constraints).setMaxLineLength(this.httpClientOptions.getMaxHeaderLineSize()).build());
    }

    private MessageConstraints.Builder getConstraintsBuilder(MessageConstraints constraints) {
        if (constraints == null) {
            return MessageConstraints.custom();
        }
        return MessageConstraints.copy(constraints);
    }
}

