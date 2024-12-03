/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.troubleshooting.stp.properties;

import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

@NotThreadSafe
public class MultiValuePropertyStore
implements PropertyStore {
    private final Map<String, List<PropertyStore>> categories = new LinkedHashMap<String, List<PropertyStore>>();
    private final Map<String, String> values = new LinkedHashMap<String, String>();

    @Override
    public void setValue(@Nonnull String name, String value) {
        this.values.put(this.escapeXmlElementName(name), StringEscapeUtils.escapeXml10((String)value));
    }

    @Override
    @Nonnull
    public Map<String, String> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    @Override
    public void putValues(@Nonnull Map<String, String> newValues) {
        for (Map.Entry<String, String> entry : newValues.entrySet()) {
            this.setValue(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Nonnull
    public PropertyStore addCategory(@Nonnull String categoryKey) {
        return this.addCategory(categoryKey, new MultiValuePropertyStore());
    }

    @Override
    public void copyCategoriesFrom(@Nonnull PropertyStore otherStore) {
        Objects.requireNonNull(otherStore);
        otherStore.getCategories().entrySet().forEach(entry -> {
            if (this.categories.containsKey(entry.getKey())) {
                List<PropertyStore> propStoreList = this.categories.get(entry.getKey());
                propStoreList.get(0).putValues(((PropertyStore)((List)entry.getValue()).get(0)).getValues());
            } else {
                this.categories.put((String)entry.getKey(), (List<PropertyStore>)entry.getValue());
            }
        });
    }

    @Override
    @Nonnull
    public PropertyStore addCategory(@Nonnull String categoryKey, @Nonnull PropertyStore store) {
        List<PropertyStore> propertyStores = this.categories.get(categoryKey);
        ArrayList<PropertyStore> localCopy = new ArrayList<PropertyStore>();
        if (propertyStores != null) {
            localCopy.addAll(propertyStores);
        }
        localCopy.add(store);
        this.categories.put(categoryKey, localCopy);
        return store;
    }

    @Override
    @Nonnull
    public Map<String, List<PropertyStore>> getCategories() {
        LinkedHashMap<String, List<PropertyStore>> transformedCategories = new LinkedHashMap<String, List<PropertyStore>>();
        for (Map.Entry<String, List<PropertyStore>> entry : this.categories.entrySet()) {
            transformedCategories.put(entry.getKey(), new ArrayList(entry.getValue()));
        }
        return transformedCategories;
    }

    private String escapeXmlElementName(String key) {
        String escapedKey = StringEscapeUtils.escapeXml((String)key);
        return escapedKey.replace(":", "-");
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("categories", this.categories).append("values", this.values).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MultiValuePropertyStore that = (MultiValuePropertyStore)o;
        return Objects.equals(this.categories, that.categories) && Objects.equals(this.values, that.values);
    }

    public int hashCode() {
        return Objects.hash(this.categories, this.values);
    }
}

