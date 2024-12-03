/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.encoder;

import java.util.HashSet;
import java.util.Set;
import org.supercsv.encoder.DefaultCsvEncoder;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

public class SelectiveCsvEncoder
extends DefaultCsvEncoder {
    private final Set<Integer> columnNumbers = new HashSet<Integer>();

    public SelectiveCsvEncoder(int ... columnsToEncode) {
        if (columnsToEncode == null) {
            throw new NullPointerException("columnsToEncode should not be null");
        }
        int[] arr$ = columnsToEncode;
        int len$ = arr$.length;
        for (int i$ = 0; i$ < len$; ++i$) {
            Integer columnToEncode = arr$[i$];
            this.columnNumbers.add(columnToEncode);
        }
    }

    public SelectiveCsvEncoder(boolean[] columnsToEncode) {
        if (columnsToEncode == null) {
            throw new NullPointerException("columnsToEncode should not be null");
        }
        for (int i = 0; i < columnsToEncode.length; ++i) {
            if (!columnsToEncode[i]) continue;
            this.columnNumbers.add(i + 1);
        }
    }

    public String encode(String input, CsvContext context, CsvPreference preference) {
        return this.columnNumbers.contains(context.getColumnNumber()) ? super.encode(input, context, preference) : input;
    }
}

