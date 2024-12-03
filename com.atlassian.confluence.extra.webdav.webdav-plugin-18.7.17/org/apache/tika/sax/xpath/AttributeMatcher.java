/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.Matcher;

public class AttributeMatcher
extends Matcher {
    public static final Matcher INSTANCE = new AttributeMatcher();

    @Override
    public boolean matchesAttribute(String namespace, String name) {
        return true;
    }
}

