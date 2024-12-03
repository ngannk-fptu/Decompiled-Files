/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import org.springframework.beans.Mergeable;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class MutablePropertyValues
implements PropertyValues,
Serializable {
    private final List<PropertyValue> propertyValueList;
    @Nullable
    private Set<String> processedProperties;
    private volatile boolean converted;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<PropertyValue>(0);
    }

    public MutablePropertyValues(@Nullable PropertyValues original) {
        if (original != null) {
            PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList<PropertyValue>(pvs.length);
            for (PropertyValue pv : pvs) {
                this.propertyValueList.add(new PropertyValue(pv));
            }
        } else {
            this.propertyValueList = new ArrayList<PropertyValue>(0);
        }
    }

    public MutablePropertyValues(@Nullable Map<?, ?> original) {
        if (original != null) {
            this.propertyValueList = new ArrayList<PropertyValue>(original.size());
            original.forEach((? super K attrName, ? super V attrValue) -> this.propertyValueList.add(new PropertyValue(attrName.toString(), attrValue)));
        } else {
            this.propertyValueList = new ArrayList<PropertyValue>(0);
        }
    }

    public MutablePropertyValues(@Nullable List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList != null ? propertyValueList : new ArrayList();
    }

    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }

    public int size() {
        return this.propertyValueList.size();
    }

    public MutablePropertyValues addPropertyValues(@Nullable PropertyValues other) {
        if (other != null) {
            PropertyValue[] pvs;
            for (PropertyValue pv : pvs = other.getPropertyValues()) {
                this.addPropertyValue(new PropertyValue(pv));
            }
        }
        return this;
    }

    public MutablePropertyValues addPropertyValues(@Nullable Map<?, ?> other) {
        if (other != null) {
            other.forEach((? super K attrName, ? super V attrValue) -> this.addPropertyValue(new PropertyValue(attrName.toString(), attrValue)));
        }
        return this;
    }

    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); ++i) {
            PropertyValue currentPv = this.propertyValueList.get(i);
            if (!currentPv.getName().equals(pv.getName())) continue;
            pv = this.mergeIfRequired(pv, currentPv);
            this.setPropertyValueAt(pv, i);
            return this;
        }
        this.propertyValueList.add(pv);
        return this;
    }

    public void addPropertyValue(String propertyName, Object propertyValue) {
        this.addPropertyValue(new PropertyValue(propertyName, propertyValue));
    }

    public MutablePropertyValues add(String propertyName, @Nullable Object propertyValue) {
        this.addPropertyValue(new PropertyValue(propertyName, propertyValue));
        return this;
    }

    public void setPropertyValueAt(PropertyValue pv, int i) {
        this.propertyValueList.set(i, pv);
    }

    private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
        Mergeable mergeable;
        Object value = newPv.getValue();
        if (value instanceof Mergeable && (mergeable = (Mergeable)value).isMergeEnabled()) {
            Object merged = mergeable.merge(currentPv.getValue());
            return new PropertyValue(newPv.getName(), merged);
        }
        return newPv;
    }

    public void removePropertyValue(PropertyValue pv) {
        this.propertyValueList.remove(pv);
    }

    public void removePropertyValue(String propertyName) {
        this.propertyValueList.remove(this.getPropertyValue(propertyName));
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableList(this.propertyValueList).iterator();
    }

    @Override
    public Spliterator<PropertyValue> spliterator() {
        return Spliterators.spliterator(this.propertyValueList, 0);
    }

    @Override
    public Stream<PropertyValue> stream() {
        return this.propertyValueList.stream();
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    @Override
    @Nullable
    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (!pv.getName().equals(propertyName)) continue;
            return pv;
        }
        return null;
    }

    @Nullable
    public Object get(String propertyName) {
        PropertyValue pv = this.getPropertyValue(propertyName);
        return pv != null ? pv.getValue() : null;
    }

    @Override
    public PropertyValues changesSince(PropertyValues old) {
        MutablePropertyValues changes = new MutablePropertyValues();
        if (old == this) {
            return changes;
        }
        for (PropertyValue newPv : this.propertyValueList) {
            PropertyValue pvOld = old.getPropertyValue(newPv.getName());
            if (pvOld != null && pvOld.equals(newPv)) continue;
            changes.addPropertyValue(newPv);
        }
        return changes;
    }

    @Override
    public boolean contains(String propertyName) {
        return this.getPropertyValue(propertyName) != null || this.processedProperties != null && this.processedProperties.contains(propertyName);
    }

    @Override
    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }

    public void registerProcessedProperty(String propertyName) {
        if (this.processedProperties == null) {
            this.processedProperties = new HashSet<String>(4);
        }
        this.processedProperties.add(propertyName);
    }

    public void clearProcessedProperty(String propertyName) {
        if (this.processedProperties != null) {
            this.processedProperties.remove(propertyName);
        }
    }

    public void setConverted() {
        this.converted = true;
    }

    public boolean isConverted() {
        return this.converted;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof MutablePropertyValues && this.propertyValueList.equals(((MutablePropertyValues)other).propertyValueList);
    }

    public int hashCode() {
        return this.propertyValueList.hashCode();
    }

    public String toString() {
        Object[] pvs = this.getPropertyValues();
        if (pvs.length > 0) {
            return "PropertyValues: length=" + pvs.length + "; " + StringUtils.arrayToDelimitedString((Object[])pvs, (String)"; ");
        }
        return "PropertyValues: length=0";
    }
}

