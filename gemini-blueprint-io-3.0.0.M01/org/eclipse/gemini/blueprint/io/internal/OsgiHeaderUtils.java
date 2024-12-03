/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.io.internal;

import java.util.ArrayList;
import org.osgi.framework.Bundle;
import org.springframework.util.StringUtils;

public abstract class OsgiHeaderUtils {
    private static final char ROUND_BRACKET_CHAR = '(';
    private static final char SQUARE_BRACKET_CHAR = '[';
    private static final char QUOTE_CHAR = '\"';
    private static final char COMMA_CHAR = ',';
    private static final String SEMI_COLON = ";";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String DEFAULT_VERSION = "0.0.0";

    public static String[] getBundleClassPath(Bundle bundle) {
        return OsgiHeaderUtils.getHeaderAsTrimmedStringArray(bundle, "Bundle-ClassPath");
    }

    public static String[] getRequireBundle(Bundle bundle) {
        return OsgiHeaderUtils.getHeaderWithAttributesAsTrimmedStringArray(bundle, "Require-Bundle");
    }

    private static String[] getHeaderAsTrimmedStringArray(Bundle bundle, String header) {
        if (bundle == null || !StringUtils.hasText((String)header)) {
            return new String[0];
        }
        String headerContent = (String)bundle.getHeaders().get(header);
        String[] entries = StringUtils.commaDelimitedListToStringArray((String)headerContent);
        for (int i = 0; i < entries.length; ++i) {
            entries[i] = entries[i].trim();
        }
        return entries;
    }

    private static String[] getHeaderWithAttributesAsTrimmedStringArray(Bundle bundle, String header) {
        if (bundle == null || !StringUtils.hasText((String)header)) {
            return new String[0];
        }
        String headerContent = (String)bundle.getHeaders().get(header);
        if (!StringUtils.hasText((String)headerContent)) {
            return new String[0];
        }
        ArrayList<String> tokens = new ArrayList<String>(2);
        StringBuilder token = new StringBuilder();
        boolean ignoreComma = false;
        for (int stringIndex = 0; stringIndex < headerContent.length(); ++stringIndex) {
            char currentChar = headerContent.charAt(stringIndex);
            if (currentChar == ',') {
                if (ignoreComma) {
                    token.append(currentChar);
                    continue;
                }
                tokens.add(token.toString().trim());
                token.delete(0, token.length());
                ignoreComma = false;
                continue;
            }
            if (currentChar == '\"') {
                ignoreComma = !ignoreComma;
            }
            token.append(currentChar);
        }
        tokens.add(token.toString().trim());
        return tokens.toArray(new String[tokens.size()]);
    }

    public static String[] parseRequiredBundleString(String entry) {
        String[] value = new String[2];
        int index = entry.indexOf(SEMI_COLON);
        if (index <= 0) {
            value[0] = entry;
            value[1] = DEFAULT_VERSION;
            return value;
        }
        value[0] = entry.substring(0, index);
        index = entry.indexOf("bundle-version");
        if (index > 0) {
            boolean isQuoted;
            int firstQuoteIndex = index + "bundle-version".length() + 1;
            boolean bl = isQuoted = entry.charAt(firstQuoteIndex) == '\"';
            if (!isQuoted) {
                int nextAttribute = entry.indexOf(SEMI_COLON, firstQuoteIndex);
                value[1] = nextAttribute > -1 ? entry.substring(firstQuoteIndex, nextAttribute) : entry.substring(firstQuoteIndex);
            } else {
                char testChar = entry.charAt(firstQuoteIndex + 1);
                boolean isRange = testChar == '[' || testChar == '(';
                int secondQuoteStartIndex = isRange ? firstQuoteIndex + 4 : firstQuoteIndex + 1;
                int numberStart = isRange ? firstQuoteIndex + 2 : firstQuoteIndex + 1;
                int numberEnd = entry.indexOf(DOUBLE_QUOTE, secondQuoteStartIndex) - (isRange ? 1 : 0);
                value[1] = entry.substring(numberStart, numberEnd);
                if (isRange) {
                    value[1] = entry.charAt(firstQuoteIndex + 1) + value[1] + entry.charAt(numberEnd);
                }
            }
        } else {
            value[1] = DEFAULT_VERSION;
        }
        return value;
    }
}

