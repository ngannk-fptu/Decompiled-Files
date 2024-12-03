/*
 * Decompiled with CFR 0.152.
 */
package org.twdata.pkgscanner.pattern;

import java.util.regex.Pattern;
import org.twdata.pkgscanner.pattern.CompiledPattern;

public class SimpleWildcardPattern
implements CompiledPattern {
    private Pattern pattern;
    private String original;

    public SimpleWildcardPattern(String pattern) {
        this.original = pattern;
        String ptn = pattern;
        ptn = ptn.replace(".", "\\.");
        ptn = ptn.replace("*", ".*");
        this.pattern = Pattern.compile(ptn);
    }

    public String getOriginal() {
        return this.original;
    }

    public boolean matches(String value) {
        return this.pattern.matcher(value).matches();
    }
}

