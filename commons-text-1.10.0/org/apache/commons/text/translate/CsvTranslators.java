/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.translate.SinglePassTranslator;

public final class CsvTranslators {
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_QUOTE = '\"';
    private static final String CSV_QUOTE_STR = String.valueOf('\"');
    private static final String CSV_ESCAPED_QUOTE_STR = CSV_QUOTE_STR + CSV_QUOTE_STR;
    private static final char[] CSV_SEARCH_CHARS = new char[]{',', '\"', '\r', '\n'};

    private CsvTranslators() {
    }

    public static class CsvUnescaper
    extends SinglePassTranslator {
        @Override
        void translateWhole(CharSequence input, Writer writer) throws IOException {
            if (input.charAt(0) != '\"' || input.charAt(input.length() - 1) != '\"') {
                writer.write(input.toString());
                return;
            }
            String quoteless = input.subSequence(1, input.length() - 1).toString();
            if (StringUtils.containsAny((CharSequence)quoteless, (char[])CSV_SEARCH_CHARS)) {
                writer.write(StringUtils.replace((String)quoteless, (String)CSV_ESCAPED_QUOTE_STR, (String)CSV_QUOTE_STR));
            } else {
                writer.write(quoteless);
            }
        }
    }

    public static class CsvEscaper
    extends SinglePassTranslator {
        @Override
        void translateWhole(CharSequence input, Writer writer) throws IOException {
            String inputSting = input.toString();
            if (StringUtils.containsNone((CharSequence)inputSting, (char[])CSV_SEARCH_CHARS)) {
                writer.write(inputSting);
            } else {
                writer.write(34);
                writer.write(StringUtils.replace((String)inputSting, (String)CSV_QUOTE_STR, (String)CSV_ESCAPED_QUOTE_STR));
                writer.write(34);
            }
        }
    }
}

