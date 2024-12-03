/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax.xpath;

import java.util.Objects;
import org.apache.tika.sax.xpath.ChildMatcher;
import org.apache.tika.sax.xpath.Matcher;

public class NamedElementMatcher
extends ChildMatcher {
    private final String namespace;
    private final String name;

    protected NamedElementMatcher(String namespace, String name, Matcher then) {
        super(then);
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public Matcher descend(String namespace, String name) {
        if (Objects.equals(namespace, this.namespace) && name.equals(this.name)) {
            return super.descend(namespace, name);
        }
        return FAIL;
    }
}

