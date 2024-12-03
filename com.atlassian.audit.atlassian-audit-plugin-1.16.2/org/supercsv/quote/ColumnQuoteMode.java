/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.quote;

import java.util.HashSet;
import java.util.Set;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.QuoteMode;
import org.supercsv.util.CsvContext;

public class ColumnQuoteMode
implements QuoteMode {
    private final Set<Integer> columnNumbers = new HashSet<Integer>();

    public ColumnQuoteMode(int ... columnsToQuote) {
        if (columnsToQuote == null) {
            throw new NullPointerException("columnsToQuote should not be null");
        }
        int[] arr$ = columnsToQuote;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; ++i$) {
            Integer columnToQuote = arr$[i$];
            this.columnNumbers.add(columnToQuote);
        }
    }

    public ColumnQuoteMode(boolean[] columnsToQuote) {
        if (columnsToQuote == null) {
            throw new NullPointerException("columnsToQuote should not be null");
        }
        for (int i = 0; i < columnsToQuote.length; ++i) {
            if (!columnsToQuote[i]) continue;
            this.columnNumbers.add(i + 1);
        }
    }

    public boolean quotesRequired(String csvColumn, CsvContext context, CsvPreference preference) {
        return this.columnNumbers.contains(context.getColumnNumber());
    }
}

