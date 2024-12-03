/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterType;
import org.bedework.util.calendar.PropertyIndex;

public abstract class AbstractPropertyFilter
extends FilterBase {
    protected PropertyIndex.PropertyInfoIndex propertyInfoIndex;

    public AbstractPropertyFilter(String name, PropertyIndex.PropertyInfoIndex propertyInfoIndex) {
        super(name);
        this.propertyInfoIndex = propertyInfoIndex;
        this.setType(FilterType.PROPERTY);
    }

    public PropertyIndex.PropertyInfoIndex getPropertyInfoIndex() {
        return this.propertyInfoIndex;
    }

    public void setPropertyInfoIndex(PropertyIndex.PropertyInfoIndex propertyInfoIndex) {
        this.propertyInfoIndex = propertyInfoIndex;
    }

    @Override
    protected FilterBase clone() {
        throw new UnsupportedOperationException("Do not support clone for this type");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getName()).append("{");
        sb.append(", propertyIndex=");
        sb.append((Object)this.getPropertyInfoIndex());
        sb.append("}");
        return sb.toString();
    }
}

