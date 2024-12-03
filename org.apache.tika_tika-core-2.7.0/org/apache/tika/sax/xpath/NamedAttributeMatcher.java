/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import java.util.Objects;
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
        return Objects.equals(namespace, this.namespace) && name.equals(this.name);
    }
}

