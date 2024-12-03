/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Uid;

public class ParameterFilter
extends FilterBase {
    private PropertyIndex.PropertyInfoIndex parentPropertyIndex;
    private PropertyIndex.ParameterInfoIndex parameterIndex;

    public ParameterFilter(String name, PropertyIndex.ParameterInfoIndex parameterIndex) {
        super(name);
        if (name == null) {
            name = Uid.getUid();
            this.setName(name);
        }
        this.setParameterIndex(parameterIndex);
    }

    public void setParameterIndex(PropertyIndex.ParameterInfoIndex val) {
        this.parameterIndex = val;
    }

    public PropertyIndex.ParameterInfoIndex getParameterIndex() {
        return this.parameterIndex;
    }

    public void setParentPropertyIndex(PropertyIndex.PropertyInfoIndex val) {
        this.parentPropertyIndex = val;
    }

    public PropertyIndex.PropertyInfoIndex getParentPropertyIndex() {
        return this.parentPropertyIndex;
    }

    @Override
    protected void toStringSegment(ToString ts) {
        super.toStringSegment(ts);
        ts.append("parameterIndex", (Object)this.getParameterIndex());
    }

    public String toString() {
        ToString ts = new ToString(this);
        super.toStringSegment(ts);
        return ts.toString();
    }
}

