/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.encoder;

import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public interface CsvEncoder {
    public String encode(String var1, CsvContext var2, CsvPreference var3);
}

