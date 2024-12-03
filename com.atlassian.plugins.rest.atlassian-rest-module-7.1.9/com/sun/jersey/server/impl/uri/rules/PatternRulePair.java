/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.uri.rules;

import com.sun.jersey.api.uri.UriPattern;

public class PatternRulePair<R> {
    public final UriPattern p;
    public final R r;

    public PatternRulePair(UriPattern p, R r) {
        this.p = p;
        this.r = r;
    }
}

