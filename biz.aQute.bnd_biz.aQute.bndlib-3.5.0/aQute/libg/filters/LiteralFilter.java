/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.filters;

import aQute.libg.filters.Filter;

public class LiteralFilter
extends Filter {
    private String filterString;

    public LiteralFilter(String filterString) {
        this.filterString = filterString;
    }

    @Override
    public void append(StringBuilder builder) {
        builder.append(this.filterString);
    }
}

