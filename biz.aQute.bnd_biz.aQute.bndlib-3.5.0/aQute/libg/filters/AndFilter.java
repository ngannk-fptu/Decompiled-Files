/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.filters;

import aQute.libg.filters.Filter;
import java.util.LinkedList;
import java.util.List;

public final class AndFilter
extends Filter {
    private final List<Filter> children = new LinkedList<Filter>();

    public AndFilter addChild(Filter child) {
        if (child instanceof AndFilter) {
            this.children.addAll(((AndFilter)child).children);
        } else {
            this.children.add(child);
        }
        return this;
    }

    @Override
    public void append(StringBuilder builder) {
        if (this.children.isEmpty()) {
            return;
        }
        builder.append("(&");
        for (Filter child : this.children) {
            child.append(builder);
        }
        builder.append(")");
    }
}

