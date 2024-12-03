/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.model;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Property {
    private final String name;
    private final List<Pattern> allowedRegExp;
    private final List<String> allowedValues;
    private final List<String> shorthandRefs;

    public Property(String name, List<Pattern> allowedRegexp3, List<String> allowedValue, List<String> shortHandRefs, String description, String onInvalidStr) {
        this.name = name;
        this.allowedRegExp = Collections.unmodifiableList(allowedRegexp3);
        this.allowedValues = Collections.unmodifiableList(allowedValue);
        this.shorthandRefs = Collections.unmodifiableList(shortHandRefs);
    }

    public List<Pattern> getAllowedRegExp() {
        return this.allowedRegExp;
    }

    public List<String> getAllowedValues() {
        return this.allowedValues;
    }

    public List<String> getShorthandRefs() {
        return this.shorthandRefs;
    }

    public String getName() {
        return this.name;
    }
}

