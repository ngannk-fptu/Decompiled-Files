/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class ConstructorArgumentValues {
    private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<Integer, ValueHolder>();
    private final List<ValueHolder> genericArgumentValues = new ArrayList<ValueHolder>();

    public ConstructorArgumentValues() {
    }

    public ConstructorArgumentValues(ConstructorArgumentValues original) {
        this.addArgumentValues(original);
    }

    public void addArgumentValues(@Nullable ConstructorArgumentValues other) {
        if (other != null) {
            other.indexedArgumentValues.forEach((index, argValue) -> this.addOrMergeIndexedArgumentValue((Integer)index, argValue.copy()));
            other.genericArgumentValues.stream().filter(valueHolder -> !this.genericArgumentValues.contains(valueHolder)).forEach(valueHolder -> this.addOrMergeGenericArgumentValue(valueHolder.copy()));
        }
    }

    public void addIndexedArgumentValue(int index, @Nullable Object value) {
        this.addIndexedArgumentValue(index, new ValueHolder(value));
    }

    public void addIndexedArgumentValue(int index, @Nullable Object value, String type) {
        this.addIndexedArgumentValue(index, new ValueHolder(value, type));
    }

    public void addIndexedArgumentValue(int index, ValueHolder newValue) {
        Assert.isTrue((index >= 0 ? 1 : 0) != 0, (String)"Index must not be negative");
        Assert.notNull((Object)newValue, (String)"ValueHolder must not be null");
        this.addOrMergeIndexedArgumentValue(index, newValue);
    }

    private void addOrMergeIndexedArgumentValue(Integer key, ValueHolder newValue) {
        Mergeable mergeable;
        ValueHolder currentValue = this.indexedArgumentValues.get(key);
        if (currentValue != null && newValue.getValue() instanceof Mergeable && (mergeable = (Mergeable)newValue.getValue()).isMergeEnabled()) {
            newValue.setValue(mergeable.merge(currentValue.getValue()));
        }
        this.indexedArgumentValues.put(key, newValue);
    }

    public boolean hasIndexedArgumentValue(int index) {
        return this.indexedArgumentValues.containsKey(index);
    }

    @Nullable
    public ValueHolder getIndexedArgumentValue(int index, @Nullable Class<?> requiredType) {
        return this.getIndexedArgumentValue(index, requiredType, null);
    }

    @Nullable
    public ValueHolder getIndexedArgumentValue(int index, @Nullable Class<?> requiredType, @Nullable String requiredName) {
        Assert.isTrue((index >= 0 ? 1 : 0) != 0, (String)"Index must not be negative");
        ValueHolder valueHolder = this.indexedArgumentValues.get(index);
        if (valueHolder != null && (valueHolder.getType() == null || requiredType != null && ClassUtils.matchesTypeName(requiredType, (String)valueHolder.getType())) && (valueHolder.getName() == null || requiredName != null && (requiredName.isEmpty() || requiredName.equals(valueHolder.getName())))) {
            return valueHolder;
        }
        return null;
    }

    public Map<Integer, ValueHolder> getIndexedArgumentValues() {
        return Collections.unmodifiableMap(this.indexedArgumentValues);
    }

    public void addGenericArgumentValue(Object value) {
        this.genericArgumentValues.add(new ValueHolder(value));
    }

    public void addGenericArgumentValue(Object value, String type) {
        this.genericArgumentValues.add(new ValueHolder(value, type));
    }

    public void addGenericArgumentValue(ValueHolder newValue) {
        Assert.notNull((Object)newValue, (String)"ValueHolder must not be null");
        if (!this.genericArgumentValues.contains(newValue)) {
            this.addOrMergeGenericArgumentValue(newValue);
        }
    }

    private void addOrMergeGenericArgumentValue(ValueHolder newValue) {
        if (newValue.getName() != null) {
            Iterator<ValueHolder> it = this.genericArgumentValues.iterator();
            while (it.hasNext()) {
                Mergeable mergeable;
                ValueHolder currentValue = it.next();
                if (!newValue.getName().equals(currentValue.getName())) continue;
                if (newValue.getValue() instanceof Mergeable && (mergeable = (Mergeable)newValue.getValue()).isMergeEnabled()) {
                    newValue.setValue(mergeable.merge(currentValue.getValue()));
                }
                it.remove();
            }
        }
        this.genericArgumentValues.add(newValue);
    }

    @Nullable
    public ValueHolder getGenericArgumentValue(Class<?> requiredType) {
        return this.getGenericArgumentValue(requiredType, null, null);
    }

    @Nullable
    public ValueHolder getGenericArgumentValue(Class<?> requiredType, String requiredName) {
        return this.getGenericArgumentValue(requiredType, requiredName, null);
    }

    @Nullable
    public ValueHolder getGenericArgumentValue(@Nullable Class<?> requiredType, @Nullable String requiredName, @Nullable Set<ValueHolder> usedValueHolders) {
        for (ValueHolder valueHolder : this.genericArgumentValues) {
            if (usedValueHolders != null && usedValueHolders.contains(valueHolder) || valueHolder.getName() != null && (requiredName == null || !requiredName.isEmpty() && !requiredName.equals(valueHolder.getName())) || valueHolder.getType() != null && (requiredType == null || !ClassUtils.matchesTypeName(requiredType, (String)valueHolder.getType())) || requiredType != null && valueHolder.getType() == null && valueHolder.getName() == null && !ClassUtils.isAssignableValue(requiredType, (Object)valueHolder.getValue())) continue;
            return valueHolder;
        }
        return null;
    }

    public List<ValueHolder> getGenericArgumentValues() {
        return Collections.unmodifiableList(this.genericArgumentValues);
    }

    @Nullable
    public ValueHolder getArgumentValue(int index, Class<?> requiredType) {
        return this.getArgumentValue(index, requiredType, null, null);
    }

    @Nullable
    public ValueHolder getArgumentValue(int index, Class<?> requiredType, String requiredName) {
        return this.getArgumentValue(index, requiredType, requiredName, null);
    }

    @Nullable
    public ValueHolder getArgumentValue(int index, @Nullable Class<?> requiredType, @Nullable String requiredName, @Nullable Set<ValueHolder> usedValueHolders) {
        Assert.isTrue((index >= 0 ? 1 : 0) != 0, (String)"Index must not be negative");
        ValueHolder valueHolder = this.getIndexedArgumentValue(index, requiredType, requiredName);
        if (valueHolder == null) {
            valueHolder = this.getGenericArgumentValue(requiredType, requiredName, usedValueHolders);
        }
        return valueHolder;
    }

    public int getArgumentCount() {
        return this.indexedArgumentValues.size() + this.genericArgumentValues.size();
    }

    public boolean isEmpty() {
        return this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty();
    }

    public void clear() {
        this.indexedArgumentValues.clear();
        this.genericArgumentValues.clear();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConstructorArgumentValues)) {
            return false;
        }
        ConstructorArgumentValues that = (ConstructorArgumentValues)other;
        if (this.genericArgumentValues.size() != that.genericArgumentValues.size() || this.indexedArgumentValues.size() != that.indexedArgumentValues.size()) {
            return false;
        }
        Iterator<ValueHolder> it1 = this.genericArgumentValues.iterator();
        Iterator<ValueHolder> it2 = that.genericArgumentValues.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            ValueHolder vh2;
            ValueHolder vh1 = it1.next();
            if (vh1.contentEquals(vh2 = it2.next())) continue;
            return false;
        }
        for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
            ValueHolder vh1 = entry.getValue();
            ValueHolder vh2 = that.indexedArgumentValues.get(entry.getKey());
            if (vh2 != null && vh1.contentEquals(vh2)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hashCode = 7;
        for (ValueHolder valueHolder : this.genericArgumentValues) {
            hashCode = 31 * hashCode + valueHolder.contentHashCode();
        }
        hashCode = 29 * hashCode;
        for (Map.Entry entry : this.indexedArgumentValues.entrySet()) {
            hashCode = 31 * hashCode + (((ValueHolder)entry.getValue()).contentHashCode() ^ ((Integer)entry.getKey()).hashCode());
        }
        return hashCode;
    }

    public static class ValueHolder
    implements BeanMetadataElement {
        @Nullable
        private Object value;
        @Nullable
        private String type;
        @Nullable
        private String name;
        @Nullable
        private Object source;
        private boolean converted = false;
        @Nullable
        private Object convertedValue;

        public ValueHolder(@Nullable Object value) {
            this.value = value;
        }

        public ValueHolder(@Nullable Object value, @Nullable String type) {
            this.value = value;
            this.type = type;
        }

        public ValueHolder(@Nullable Object value, @Nullable String type, @Nullable String name) {
            this.value = value;
            this.type = type;
            this.name = name;
        }

        public void setValue(@Nullable Object value) {
            this.value = value;
        }

        @Nullable
        public Object getValue() {
            return this.value;
        }

        public void setType(@Nullable String type) {
            this.type = type;
        }

        @Nullable
        public String getType() {
            return this.type;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public String getName() {
            return this.name;
        }

        public void setSource(@Nullable Object source) {
            this.source = source;
        }

        @Override
        @Nullable
        public Object getSource() {
            return this.source;
        }

        public synchronized boolean isConverted() {
            return this.converted;
        }

        public synchronized void setConvertedValue(@Nullable Object value) {
            this.converted = value != null;
            this.convertedValue = value;
        }

        @Nullable
        public synchronized Object getConvertedValue() {
            return this.convertedValue;
        }

        private boolean contentEquals(ValueHolder other) {
            return this == other || ObjectUtils.nullSafeEquals((Object)this.value, (Object)other.value) && ObjectUtils.nullSafeEquals((Object)this.type, (Object)other.type);
        }

        private int contentHashCode() {
            return ObjectUtils.nullSafeHashCode((Object)this.value) * 29 + ObjectUtils.nullSafeHashCode((Object)this.type);
        }

        public ValueHolder copy() {
            ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
            copy.setSource(this.source);
            return copy;
        }
    }
}

