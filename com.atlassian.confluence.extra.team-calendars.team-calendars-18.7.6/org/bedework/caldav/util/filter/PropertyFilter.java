/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import java.util.Collections;
import java.util.List;
import org.bedework.caldav.util.filter.FilterBase;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.util.misc.ToString;
import org.bedework.util.misc.Uid;

public class PropertyFilter
extends FilterBase {
    private PropertyIndex.PropertyInfoIndex parentPropertyIndex;
    private PropertyIndex.PropertyInfoIndex propertyIndex;
    private List<PropertyIndex.PropertyInfoIndex> propertyIndexes;
    private String strKey;
    private Integer intKey;

    public PropertyFilter(String name, PropertyIndex.PropertyInfoIndex propertyIndex) {
        this(name, propertyIndex, null, null);
    }

    public PropertyFilter(String name, PropertyIndex.PropertyInfoIndex propertyIndex, Integer intKey, String strKey) {
        super(name);
        if (name == null) {
            name = Uid.getUid();
            this.setName(name);
        }
        this.propertyIndex = propertyIndex;
        this.propertyIndexes = Collections.singletonList(propertyIndex);
        this.intKey = intKey;
        this.strKey = strKey;
    }

    public PropertyFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes) {
        this(name, propertyIndexes, null, null);
    }

    public PropertyFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes, Integer intKey, String strKey) {
        super(name);
        if (name == null) {
            this.setName(Uid.getUid());
        }
        this.propertyIndexes = propertyIndexes;
        if (propertyIndexes.size() == 1) {
            this.propertyIndex = propertyIndexes.get(0);
            return;
        }
        if (propertyIndexes.size() != 2) {
            throw new RuntimeException("Not implemented - subfield depth > 2");
        }
        this.propertyIndex = propertyIndexes.get(1);
        this.setParentPropertyIndex(propertyIndexes.get(0));
        this.intKey = intKey;
        this.strKey = strKey;
    }

    protected void setPropertyIndex(PropertyIndex.PropertyInfoIndex val) {
        this.propertyIndex = val;
    }

    public PropertyIndex.PropertyInfoIndex getPropertyIndex() {
        return this.propertyIndex;
    }

    public void setParentPropertyIndex(PropertyIndex.PropertyInfoIndex val) {
        this.parentPropertyIndex = val;
    }

    public PropertyIndex.PropertyInfoIndex getParentPropertyIndex() {
        return this.parentPropertyIndex;
    }

    public List<PropertyIndex.PropertyInfoIndex> getPropertyIndexes() {
        return this.propertyIndexes;
    }

    public Integer getIntKey() {
        return this.intKey;
    }

    public String getStrKey() {
        return this.strKey;
    }

    @Override
    protected void toStringSegment(ToString ts) {
        super.toStringSegment(ts);
        ts.append("propertyIndex", (Object)this.getPropertyIndex());
    }

    public String toString() {
        ToString ts = new ToString(this);
        this.toStringSegment(ts);
        return ts.toString();
    }
}

