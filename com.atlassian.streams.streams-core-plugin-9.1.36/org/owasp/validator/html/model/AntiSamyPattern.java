/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AntiSamyPattern {
    private final Pattern pattern;

    public AntiSamyPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return this.pattern;
    }

    public Matcher matcher(CharSequence input) {
        return this.pattern.matcher(input);
    }

    public boolean matches(String other) {
        return this.matcher(other).matches();
    }
}

