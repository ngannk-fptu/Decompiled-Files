/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  zipkin2.Span
 *  zipkin2.codec.SpanBytesEncoder
 *  zipkin2.reporter.Reporter
 */
package com.atlassian.confluence.util.zipkin.impl;

import com.atlassian.confluence.util.logging.LoggingContext;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin2.Span;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.Reporter;

public class LoggingSpanReporter
implements Reporter<Span> {
    private static final Logger log = LoggerFactory.getLogger(LoggingSpanReporter.class);

    public void report(Span span) {
        LoggingContext.executeWithContext("trace", writer -> writer.write(new String(SpanBytesEncoder.JSON_V2.encode((Object)span), StandardCharsets.UTF_8)), () -> log.info("Zipkin span"));
    }
}

