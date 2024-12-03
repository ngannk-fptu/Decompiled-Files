/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.filters;

import aQute.libg.filters.Filter;
import aQute.libg.filters.Operator;

public final class SimpleFilter
extends Filter {
    private final String name;
    private final Operator operator;
    private final String value;

    public SimpleFilter(String name, String value) {
        this(name, Operator.Equals, value);
    }

    public SimpleFilter(String name, Operator operator, String value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public void append(StringBuilder builder) {
        builder.append('(');
        builder.append(this.name).append(this.operator.getSymbol()).append(this.value);
        builder.append(')');
    }
}

