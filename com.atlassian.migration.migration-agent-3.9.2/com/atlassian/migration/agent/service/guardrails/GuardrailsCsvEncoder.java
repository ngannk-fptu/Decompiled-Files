/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.supercsv.encoder.CsvEncoder
 *  org.supercsv.prefs.CsvPreference
 *  org.supercsv.util.CsvContext
 */
package com.atlassian.migration.agent.service.guardrails;

import org.supercsv.encoder.CsvEncoder;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public class GuardrailsCsvEncoder
implements CsvEncoder {
    public String encode(String input, CsvContext context, CsvPreference preference) {
        boolean quotesRequiredForSurroundingSpaces;
        StringBuilder currentColumn = new StringBuilder();
        int delimiter = preference.getDelimiterChar();
        char quote = preference.getQuoteChar();
        String eolSymbols = preference.getEndOfLineSymbols();
        boolean quotesRequiredForSpecialChar = this.processInput(input, context, currentColumn, delimiter, quote, eolSymbols);
        boolean quotesRequiredForMode = preference.getQuoteMode().quotesRequired(input, context, preference);
        boolean bl = quotesRequiredForSurroundingSpaces = preference.isSurroundingSpacesNeedQuotes() && input.length() > 0 && (input.charAt(0) == ' ' || input.charAt(input.length() - 1) == ' ');
        if (quotesRequiredForSpecialChar || quotesRequiredForMode || quotesRequiredForSurroundingSpaces) {
            currentColumn.insert(0, quote).append(quote);
        }
        return currentColumn.toString();
    }

    private boolean processInput(String input, CsvContext context, StringBuilder currentColumn, int delimiter, char quote, String eolSymbols) {
        boolean quotesRequiredForSpecialChar = false;
        boolean skipNewline = false;
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (skipNewline) {
                skipNewline = this.handleSkipNewline(c);
                continue;
            }
            if (this.isDelimiterOrQuote(c, delimiter, quote)) {
                quotesRequiredForSpecialChar = this.processDelimiterOrQuote(c, currentColumn, quote);
                continue;
            }
            if (this.isCarriageReturnOrNewline(c)) {
                quotesRequiredForSpecialChar = this.processCarriageReturnOrNewline(currentColumn, eolSymbols, context);
                if (c != '\r') continue;
                skipNewline = true;
                continue;
            }
            currentColumn.append(c);
        }
        return quotesRequiredForSpecialChar;
    }

    private boolean handleSkipNewline(char c) {
        return c != '\n';
    }

    private boolean isDelimiterOrQuote(char c, int delimiter, char quote) {
        return c == delimiter || c == quote;
    }

    private boolean processDelimiterOrQuote(char c, StringBuilder currentColumn, char quote) {
        if (c == quote) {
            currentColumn.append("\\");
        }
        currentColumn.append(c);
        return true;
    }

    private boolean isCarriageReturnOrNewline(char c) {
        return c == '\r' || c == '\n';
    }

    private boolean processCarriageReturnOrNewline(StringBuilder currentColumn, String eolSymbols, CsvContext context) {
        currentColumn.append(eolSymbols);
        context.setLineNumber(context.getLineNumber() + 1);
        return true;
    }
}

