/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources.comparators;

import java.util.Comparator;
import java.util.Optional;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;

public class Reverse
extends ResourceComparator {
    private static final String ONE_NESTED = "You must not nest more than one ResourceComparator for reversal.";
    private ResourceComparator nested;

    public Reverse() {
    }

    public Reverse(ResourceComparator c) {
        this.add(c);
    }

    public void add(ResourceComparator c) {
        if (this.nested != null) {
            throw new BuildException(ONE_NESTED);
        }
        this.nested = c;
        this.setChecked(false);
    }

    @Override
    protected int resourceCompare(Resource foo, Resource bar) {
        return Optional.ofNullable(this.nested).orElseGet(Comparator::naturalOrder).reversed().compare(foo, bar);
    }

    @Override
    protected void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            if (this.nested != null) {
                Reverse.pushAndInvokeCircularReferenceCheck(this.nested, stk, p);
            }
            this.setChecked(true);
        }
    }
}

