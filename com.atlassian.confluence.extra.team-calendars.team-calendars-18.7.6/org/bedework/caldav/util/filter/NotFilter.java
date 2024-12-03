/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.util.misc.ToString;

public class NotFilter
extends FilterBase {
    public NotFilter() {
        super("NOT");
    }

    public String toString() {
        ToString ts = new ToString(this);
        super.toStringSegment(ts);
        ts.append("children", this.getChildren());
        return ts.toString();
    }
}

