/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.regex;

import org.radeox.regex.JdkMatcher;
import org.radeox.regex.Pattern;
import org.radeox.regex.Substitution;

public abstract class Matcher {
    public static Matcher create(String input, Pattern pattern) {
        return new JdkMatcher(input, pattern);
    }

    public abstract String substitute(Substitution var1);

    public abstract String substitute(String var1);

    public abstract boolean matches();

    public abstract boolean contains();
}

