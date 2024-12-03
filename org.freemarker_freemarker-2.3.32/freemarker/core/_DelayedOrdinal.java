/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;

public class _DelayedOrdinal
extends _DelayedConversionToString {
    public _DelayedOrdinal(Object object) {
        super(object);
    }

    @Override
    protected String doConversion(Object obj) {
        if (obj instanceof Number) {
            long n = ((Number)obj).longValue();
            if (n % 10L == 1L && n % 100L != 11L) {
                return n + "st";
            }
            if (n % 10L == 2L && n % 100L != 12L) {
                return n + "nd";
            }
            if (n % 10L == 3L && n % 100L != 13L) {
                return n + "rd";
            }
            return n + "th";
        }
        return "" + obj;
    }
}

