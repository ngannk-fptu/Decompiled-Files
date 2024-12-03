/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.filter;

import aQute.lib.filter.Filter;

public class ExtendedFilter
extends Filter {
    public ExtendedFilter(String filter) throws IllegalArgumentException {
        super(ExtendedFilter.cleanup(filter), true);
    }

    public static String cleanup(String s) {
        if (s == null) {
            return null;
        }
        if ((s = s.trim()).startsWith("(") && s.endsWith(")")) {
            return s;
        }
        return "(" + s + ")";
    }
}

