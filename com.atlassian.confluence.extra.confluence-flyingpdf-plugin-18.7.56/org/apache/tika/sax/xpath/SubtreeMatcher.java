/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.CompositeMatcher;
import org.apache.tika.sax.xpath.Matcher;

public class SubtreeMatcher
extends Matcher {
    private final Matcher then;

    public SubtreeMatcher(Matcher then) {
        this.then = then;
    }

    @Override
    public Matcher descend(String namespace, String name) {
        Matcher next = this.then.descend(namespace, name);
        if (next == FAIL || next == this.then) {
            return this;
        }
        return new CompositeMatcher(next, this);
    }

    @Override
    public boolean matchesElement() {
        return this.then.matchesElement();
    }

    @Override
    public boolean matchesAttribute(String namespace, String name) {
        return this.then.matchesAttribute(namespace, name);
    }

    @Override
    public boolean matchesText() {
        return this.then.matchesText();
    }
}

