/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.schema.spi;

import java.util.function.Function;

final class IdUtils {
    private static final Function<Character, String> CAMEL_CASER = input -> String.valueOf(Character.toUpperCase(input.charValue()));
    private static final Function<Character, String> TITLER = input -> " " + Character.toUpperCase(input.charValue());

    IdUtils() {
    }

    public static String dashesToCamelCase(String id) {
        return IdUtils.process(id, CAMEL_CASER);
    }

    public static String dashesToTitle(String id) {
        return IdUtils.process(id, TITLER).substring(1);
    }

    private static String process(String id, Function<Character, String> converter) {
        StringBuilder sb = new StringBuilder();
        int prev = 45;
        for (int x = 0; x < id.length(); ++x) {
            char cur = id.charAt(x);
            if (prev == 45) {
                sb.append(converter.apply(Character.valueOf(cur)));
            } else if (cur != '-') {
                sb.append(cur);
            }
            prev = cur;
        }
        return sb.toString();
    }
}

