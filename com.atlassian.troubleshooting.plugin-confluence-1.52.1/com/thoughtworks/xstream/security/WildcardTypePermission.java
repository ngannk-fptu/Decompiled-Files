/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.RegExpTypePermission;

public class WildcardTypePermission
extends RegExpTypePermission {
    public WildcardTypePermission(String[] patterns) {
        this(false, patterns);
    }

    public WildcardTypePermission(boolean allowAnonymous, String[] patterns) {
        super(WildcardTypePermission.getRegExpPatterns(patterns, allowAnonymous));
    }

    private static String[] getRegExpPatterns(String[] wildcards, boolean allowAnonymous) {
        if (wildcards == null) {
            return null;
        }
        String[] regexps = new String[wildcards.length];
        for (int i = 0; i < wildcards.length; ++i) {
            String wildcardExpression = wildcards[i];
            StringBuffer result = new StringBuffer(wildcardExpression.length() * 2);
            result.append("(?u)");
            int length = wildcardExpression.length();
            block6: for (int j = 0; j < length; ++j) {
                char ch = wildcardExpression.charAt(j);
                switch (ch) {
                    case '$': 
                    case '(': 
                    case ')': 
                    case '+': 
                    case '.': 
                    case '[': 
                    case '\\': 
                    case ']': 
                    case '^': 
                    case '|': {
                        result.append('\\').append(ch);
                        continue block6;
                    }
                    case '?': {
                        result.append('.');
                        continue block6;
                    }
                    case '*': {
                        if (j + 1 < length && wildcardExpression.charAt(j + 1) == '*') {
                            result.append(allowAnonymous ? "[\\P{C}]*" : "[\\P{C}&&[^$]]*(?:\\$[^0-9$][\\P{C}&&[^.$]]*)*");
                            ++j;
                            continue block6;
                        }
                        result.append(allowAnonymous ? "[\\P{C}&&[^.]]*" : "[\\P{C}&&[^.$]]*(?:\\$[^0-9$][\\P{C}&&[^.$]]*)*");
                        continue block6;
                    }
                    default: {
                        result.append(ch);
                    }
                }
            }
            regexps[i] = result.toString();
        }
        return regexps;
    }
}

