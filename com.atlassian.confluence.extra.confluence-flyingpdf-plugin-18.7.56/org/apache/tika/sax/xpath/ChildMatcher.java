/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.Matcher;

public class ChildMatcher
extends Matcher {
    private final Matcher then;

    public ChildMatcher(Matcher then) {
        this.then = then;
    }

    @Override
    public Matcher descend(String namespace, String name) {
        return this.then;
    }
}

