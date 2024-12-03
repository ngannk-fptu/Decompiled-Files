/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.Matcher;

public class NodeMatcher
extends Matcher {
    public static final Matcher INSTANCE = new NodeMatcher();

    @Override
    public boolean matchesElement() {
        return true;
    }

    @Override
    public boolean matchesAttribute(String namespace, String name) {
        return true;
    }

    @Override
    public boolean matchesText() {
        return true;
    }
}

