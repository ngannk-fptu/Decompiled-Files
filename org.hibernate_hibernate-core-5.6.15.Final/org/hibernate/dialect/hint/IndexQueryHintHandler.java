/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.hint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.dialect.hint.QueryHintHandler;

public class IndexQueryHintHandler
implements QueryHintHandler {
    public static final IndexQueryHintHandler INSTANCE = new IndexQueryHintHandler();
    private static final Pattern QUERY_PATTERN = Pattern.compile("^(select.*?from.*?)(where.*?)$");

    @Override
    public String addQueryHints(String query, String hints) {
        Matcher matcher = QUERY_PATTERN.matcher(query);
        if (matcher.matches() && matcher.groupCount() > 1) {
            String startToken = matcher.group(1);
            String endToken = matcher.group(2);
            return startToken + " use index (" + hints + ") " + endToken;
        }
        return query;
    }
}

