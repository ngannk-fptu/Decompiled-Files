/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.encoder;

import org.supercsv.encoder.CsvEncoder;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public class DefaultCsvEncoder
implements CsvEncoder {
    private final StringBuilder currentColumn = new StringBuilder();

    public String encode(String input, CsvContext context, CsvPreference preference) {
        boolean quotesRequiredForSurroundingSpaces;
        this.currentColumn.delete(0, this.currentColumn.length());
        int delimiter = preference.getDelimiterChar();
        char quote = (char)preference.getQuoteChar();
        String eolSymbols = preference.getEndOfLineSymbols();
        int lastCharIndex = input.length() - 1;
        boolean quotesRequiredForSpecialChar = false;
        boolean skipNewline = false;
        for (int i = 0; i <= lastCharIndex; ++i) {
            char c = input.charAt(i);
            if (skipNewline) {
                skipNewline = false;
                if (c == '\n') continue;
            }
            if (c == delimiter) {
                quotesRequiredForSpecialChar = true;
                this.currentColumn.append(c);
                continue;
            }
            if (c == quote) {
                quotesRequiredForSpecialChar = true;
                this.currentColumn.append(quote);
                this.currentColumn.append(quote);
                continue;
            }
            if (c == '\r') {
                quotesRequiredForSpecialChar = true;
                this.currentColumn.append(eolSymbols);
                context.setLineNumber(context.getLineNumber() + 1);
                skipNewline = true;
                continue;
            }
            if (c == '\n') {
                quotesRequiredForSpecialChar = true;
                this.currentColumn.append(eolSymbols);
                context.setLineNumber(context.getLineNumber() + 1);
                continue;
            }
            this.currentColumn.append(c);
        }
        boolean quotesRequiredForMode = preference.getQuoteMode().quotesRequired(input, context, preference);
        boolean bl = quotesRequiredForSurroundingSpaces = preference.isSurroundingSpacesNeedQuotes() && input.length() > 0 && (input.charAt(0) == ' ' || input.charAt(input.length() - 1) == ' ');
        if (quotesRequiredForSpecialChar || quotesRequiredForMode || quotesRequiredForSurroundingSpaces) {
            this.currentColumn.insert(0, quote).append(quote);
        }
        return this.currentColumn.toString();
    }
}

