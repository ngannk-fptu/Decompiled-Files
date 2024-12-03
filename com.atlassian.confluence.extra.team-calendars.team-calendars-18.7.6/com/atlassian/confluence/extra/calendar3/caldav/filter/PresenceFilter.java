/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.AbstractPropertyFilter;
import org.bedework.util.calendar.PropertyIndex;

public class PresenceFilter
extends AbstractPropertyFilter {
    private boolean isExist;

    public PresenceFilter(PropertyIndex.PropertyInfoIndex propertyInfoIndex, boolean isExist) {
        super("PresenceFilter", propertyInfoIndex);
        this.isExist = isExist;
    }

    public boolean isExist() {
        return this.isExist;
    }

    public void setExist(boolean exist) {
        this.isExist = exist;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("PresenceFilter{");
        sb.append("propertyIndex=");
        sb.append((Object)this.getPropertyInfoIndex());
        if (this.isExist()) {
            sb.append("\nproperty not null");
        } else {
            sb.append("\nproperty null");
        }
        sb.append("}");
        return sb.toString();
    }
}

