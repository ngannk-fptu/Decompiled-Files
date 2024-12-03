/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.utils;

import java.util.regex.PatternSyntaxException;

public class StringMatchingPatternSyntaxException
extends Exception {
    private static final long serialVersionUID = 4616654570576723975L;

    public StringMatchingPatternSyntaxException(PatternSyntaxException e) {
        super(e);
    }
}

