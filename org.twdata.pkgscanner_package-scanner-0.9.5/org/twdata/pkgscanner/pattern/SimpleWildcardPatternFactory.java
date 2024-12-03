/*
 * Decompiled with CFR 0.152.
 */
package org.twdata.pkgscanner.pattern;

import org.twdata.pkgscanner.pattern.CompiledPattern;
import org.twdata.pkgscanner.pattern.PatternFactory;
import org.twdata.pkgscanner.pattern.SimpleWildcardPattern;

public class SimpleWildcardPatternFactory
implements PatternFactory {
    public CompiledPattern compile(String pattern) {
        return new SimpleWildcardPattern(pattern);
    }
}

