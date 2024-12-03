/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import java.util.List;
import org.bedework.caldav.util.filter.PropertyFilter;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.util.misc.ToString;

public class PresenceFilter
extends PropertyFilter {
    private boolean testPresent;

    public PresenceFilter(String name, PropertyIndex.PropertyInfoIndex propertyIndex, boolean testPresent) {
        super(name, propertyIndex);
        this.testPresent = testPresent;
    }

    public PresenceFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes, boolean testPresent) {
        super(name, propertyIndexes);
        this.testPresent = testPresent;
    }

    public PresenceFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes, boolean testPresent, Integer intKey, String strKey) {
        super(name, propertyIndexes, intKey, strKey);
        this.testPresent = testPresent;
    }

    public boolean getTestPresent() {
        return this.testPresent;
    }

    @Override
    public String toString() {
        ToString ts = new ToString(this);
        super.toStringSegment(ts);
        ts.append("testPresent", this.getTestPresent());
        return ts.toString();
    }
}

