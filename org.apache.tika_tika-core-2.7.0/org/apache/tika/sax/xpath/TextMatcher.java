/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.Matcher;

public class TextMatcher
extends Matcher {
    public static final Matcher INSTANCE = new TextMatcher();

    @Override
    public boolean matchesText() {
        return true;
    }
}

