/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.plugin.util;

import com.atlassian.annotations.Internal;
import java.util.Collection;

@Internal
public class RegularExpressions {
    public static String anyOf(Collection<String> expressions) {
        if (expressions.isEmpty()) {
            return ".\\A";
        }
        int capacity = 1 + 5 * expressions.size() + 2;
        for (String expression : expressions) {
            capacity += expression.length();
        }
        StringBuilder compound = new StringBuilder(capacity);
        compound.append('(');
        for (String expression : expressions) {
            compound.append(")|(?:");
            compound.append(expression);
        }
        compound.append("))");
        compound.setCharAt(1, '?');
        compound.setCharAt(2, ':');
        return compound.toString();
    }
}

