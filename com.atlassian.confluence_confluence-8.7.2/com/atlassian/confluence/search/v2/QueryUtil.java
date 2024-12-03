/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

public class QueryUtil {
    public static String escape(String query, char ... characters) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < query.length(); ++i) {
            char c = query.charAt(i);
            for (int j = 0; j < characters.length; ++j) {
                if (c != characters[j]) continue;
                sb.append('\\');
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String escape(String query) {
        return QueryUtil.escape(query, '\\', '/', '<', '>');
    }
}

