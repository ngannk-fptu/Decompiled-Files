/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.util.filter;

import java.util.List;
import org.bedework.caldav.util.TimeRange;
import org.bedework.caldav.util.filter.PropertyFilter;
import org.bedework.caldav.util.filter.TimeRangeFilter;
import org.bedework.util.calendar.PropertyIndex;
import org.bedework.util.misc.ToString;

public class ObjectFilter<T>
extends PropertyFilter {
    private T entity;
    private boolean exact = true;
    private boolean caseless = true;
    private boolean prefix;

    public ObjectFilter(String name, PropertyIndex.PropertyInfoIndex propertyIndex) {
        this(name, propertyIndex, null, null);
    }

    public ObjectFilter(String name, PropertyIndex.PropertyInfoIndex propertyIndex, Integer intKey, String strKey) {
        super(name, propertyIndex, intKey, strKey);
    }

    public ObjectFilter(PropertyIndex.PropertyInfoIndex propertyIndex, T val) {
        super(null, propertyIndex);
        this.setEntity(val);
    }

    public ObjectFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes) {
        this(name, propertyIndexes, null, null);
    }

    public ObjectFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes, Integer intKey, String strKey) {
        super(name, propertyIndexes, intKey, strKey);
    }

    public void setEntity(T val) {
        this.entity = val;
    }

    public T getEntity() {
        return this.entity;
    }

    public void setExact(boolean val) {
        this.exact = val;
    }

    public boolean getExact() {
        return this.exact;
    }

    public void setCaseless(boolean val) {
        this.caseless = val;
    }

    public boolean getCaseless() {
        return this.caseless;
    }

    public void setPrefixMatch(boolean val) {
        this.prefix = val;
    }

    public boolean getPrefixMatch() {
        return this.prefix;
    }

    public static ObjectFilter<String> makeFilter(PropertyIndex.PropertyInfoIndex propertyIndex, String val) {
        ObjectFilter<String> of = new ObjectFilter<String>(null, propertyIndex, null, null);
        of.setEntity(val);
        return of;
    }

    public static ObjectFilter makeFilter(String name, PropertyIndex.PropertyInfoIndex propertyIndex, TimeRange val, Integer intKey, String strKey) {
        TimeRangeFilter trf = new TimeRangeFilter(name, propertyIndex, intKey, strKey);
        trf.setEntity(val);
        return trf;
    }

    public static ObjectFilter makeFilter(String name, List<PropertyIndex.PropertyInfoIndex> propertyIndexes, TimeRange val, Integer intKey, String strKey) {
        if (propertyIndexes.size() == 1) {
            return ObjectFilter.makeFilter(name, propertyIndexes.get(0), val, intKey, strKey);
        }
        if (propertyIndexes.size() != 2) {
            throw new RuntimeException("Not implemented - subfield depth > 2");
        }
        TimeRangeFilter trf = new TimeRangeFilter(name, propertyIndexes, intKey, strKey);
        trf.setParentPropertyIndex(propertyIndexes.get(0));
        trf.setEntity(val);
        return trf;
    }

    @Override
    public String toString() {
        ToString ts = new ToString(this);
        ts.append(this.getEntity());
        return ts.toString();
    }
}

