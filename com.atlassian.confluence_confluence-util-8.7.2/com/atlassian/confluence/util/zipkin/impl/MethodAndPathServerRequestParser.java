/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.http.HttpRequest
 *  brave.http.HttpRequestParser$Default
 *  brave.propagation.TraceContext
 */
package com.atlassian.confluence.util.zipkin.impl;

import brave.http.HttpRequest;
import brave.http.HttpRequestParser;
import brave.propagation.TraceContext;

public class MethodAndPathServerRequestParser
extends HttpRequestParser.Default {
    protected String spanName(HttpRequest req, TraceContext context) {
        return req.method() + " " + req.path();
    }
}

