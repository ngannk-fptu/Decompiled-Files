/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.Matcher;

public class CompositeMatcher
extends Matcher {
    private final Matcher a;
    private final Matcher b;

    public CompositeMatcher(Matcher a, Matcher b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Matcher descend(String namespace, String name) {
        Matcher a = this.a.descend(namespace, name);
        Matcher b = this.b.descend(namespace, name);
        if (a == FAIL) {
            return b;
        }
        if (b == FAIL) {
            return a;
        }
        if (this.a == a && this.b == b) {
            return this;
        }
        return new CompositeMatcher(a, b);
    }

    @Override
    public boolean matchesElement() {
        return this.a.matchesElement() || this.b.matchesElement();
    }

    @Override
    public boolean matchesAttribute(String namespace, String name) {
        return this.a.matchesAttribute(namespace, name) || this.b.matchesAttribute(namespace, name);
    }

    @Override
    public boolean matchesText() {
        return this.a.matchesText() || this.b.matchesText();
    }
}

