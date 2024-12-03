/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import org.apache.tika.sax.xpath.Matcher;

public class NamedAttributeMatcher
extends Matcher {
    private final String namespace;
    private final String name;

    public NamedAttributeMatcher(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public boolean matchesAttribute(String namespace, String name) {
        return NamedAttributeMatcher.equals(namespace, this.namespace) && name.equals(this.name);
    }

    private static boolean equals(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}

