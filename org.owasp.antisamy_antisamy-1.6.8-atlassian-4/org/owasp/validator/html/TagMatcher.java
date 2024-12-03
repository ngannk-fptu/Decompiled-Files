/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html;

import java.util.HashSet;
import java.util.Set;

public class TagMatcher {
    private final Set<String> allowedLowercase = new HashSet<String>();

    public TagMatcher(Iterable<String> allowedValues) {
        for (String item : allowedValues) {
            this.allowedLowercase.add(item.toLowerCase());
        }
    }

    public boolean matches(String tagName) {
        return this.allowedLowercase.contains(tagName.toLowerCase());
    }

    public int size() {
        return this.allowedLowercase.size();
    }
}

