/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.spi.uri.rules;

import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import java.util.Iterator;

public interface UriRules<R> {
    public Iterator<R> match(CharSequence var1, UriMatchResultContext var2);
}

