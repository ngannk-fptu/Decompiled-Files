/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.Matcher;

public class ElementMatcher
extends Matcher {
    public static final Matcher INSTANCE = new ElementMatcher();

    @Override
    public boolean matchesElement() {
        return true;
    }
}

