/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.quote;

import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public interface QuoteMode {
    public boolean quotesRequired(String var1, CsvContext var2, CsvPreference var3);
}

