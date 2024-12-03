/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.quote;

import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.QuoteMode;
import org.supercsv.util.CsvContext;

public class AlwaysQuoteMode
implements QuoteMode {
    public boolean quotesRequired(String csvColumn, CsvContext context, CsvPreference preference) {
        return true;
    }
}

