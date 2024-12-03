/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.util.misc.ToString;

public class BooleanFilter
extends FilterBase {
    private final boolean val;
    public static final BooleanFilter falseFilter = new BooleanFilter(false);
    public static final BooleanFilter trueFilter = new BooleanFilter(true);

    public BooleanFilter(boolean val) {
        super("Boolean");
        this.val = val;
    }

    public boolean getValue() {
        return this.val;
    }

    public String toString() {
        ToString ts = new ToString(this);
        super.toStringSegment(ts);
        ts.append("value", this.getValue());
        ts.append("children", this.getChildren());
        return ts.toString();
    }
}

