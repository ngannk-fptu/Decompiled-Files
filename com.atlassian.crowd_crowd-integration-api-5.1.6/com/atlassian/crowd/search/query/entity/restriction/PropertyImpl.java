/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.search.query.entity.restriction.Property;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PropertyImpl<V>
implements Property<V> {
    private final String propertyName;
    private final Class<V> propertyType;

    public PropertyImpl(String propertyName, Class<V> propertyType) {
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    @Override
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    public Class<V> getPropertyType() {
        return this.propertyType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Property)) {
            return false;
        }
        Property property = (Property)o;
        if (this.propertyName != null ? !this.propertyName.equals(property.getPropertyName()) : property.getPropertyName() != null) {
            return false;
        }
        return !(this.propertyType != null ? !this.propertyType.equals(property.getPropertyType()) : property.getPropertyType() != null);
    }

    public int hashCode() {
        int result = this.propertyName != null ? this.propertyName.hashCode() : 0;
        result = 31 * result + (this.propertyType != null ? this.propertyType.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("propertyName", (Object)this.propertyName).append("propertyType", this.propertyType).toString();
    }
}

