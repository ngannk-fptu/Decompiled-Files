/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.regex.Matcher;

public class RegexSupport {
    private static final ThreadLocal CURRENT_MATCHER = new ThreadLocal();

    public static Matcher getLastMatcher() {
        return (Matcher)CURRENT_MATCHER.get();
    }

    public static void setLastMatcher(Matcher matcher) {
        CURRENT_MATCHER.set(matcher);
    }
}

