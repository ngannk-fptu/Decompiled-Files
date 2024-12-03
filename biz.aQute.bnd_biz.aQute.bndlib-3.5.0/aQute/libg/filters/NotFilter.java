/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.filters;

import aQute.libg.filters.Filter;

public final class NotFilter
extends Filter {
    private final Filter child;

    public NotFilter(Filter child) {
        this.child = child;
    }

    @Override
    public void append(StringBuilder builder) {
        builder.append("(!");
        this.child.append(builder);
        builder.append(")");
    }
}

