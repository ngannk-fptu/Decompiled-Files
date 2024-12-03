/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.ja.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CSVUtil {
    private static final char QUOTE = '\"';
    private static final char COMMA = ',';
    private static final Pattern QUOTE_REPLACE_PATTERN = Pattern.compile("^\"([^\"]+)\"$");
    private static final String ESCAPED_QUOTE = "\"\"";

    private CSVUtil() {
    }

    public static String[] parse(String line) {
        boolean insideQuote = false;
        ArrayList<String> result = new ArrayList<String>();
        int quoteCount = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (c == '\"') {
                insideQuote = !insideQuote;
                ++quoteCount;
            }
            if (c == ',' && !insideQuote) {
                String value = sb.toString();
                value = CSVUtil.unQuoteUnEscape(value);
                result.add(value);
                sb.setLength(0);
                continue;
            }
            sb.append(c);
        }
        result.add(sb.toString());
        if (quoteCount % 2 != 0) {
            return new String[0];
        }
        return result.toArray(new String[result.size()]);
    }

    private static String unQuoteUnEscape(String original) {
        String result = original;
        if (result.indexOf(34) >= 0) {
            Matcher m = QUOTE_REPLACE_PATTERN.matcher(original);
            if (m.matches()) {
                result = m.group(1);
            }
            if (result.indexOf(ESCAPED_QUOTE) >= 0) {
                result = result.replace(ESCAPED_QUOTE, "\"");
            }
        }
        return result;
    }

    public static String quoteEscape(String original) {
        String result = original;
        if (result.indexOf(34) >= 0) {
            result.replace("\"", ESCAPED_QUOTE);
        }
        if (result.indexOf(44) >= 0) {
            result = "\"" + result + "\"";
        }
        return result;
    }
}

