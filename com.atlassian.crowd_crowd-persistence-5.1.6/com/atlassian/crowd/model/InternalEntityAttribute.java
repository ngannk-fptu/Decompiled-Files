/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.collectors.CollectorsUtil
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.util.InternalEntityUtils
 *  com.google.common.collect.SetMultimap
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model;

import com.atlassian.collectors.CollectorsUtil;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.util.InternalEntityUtils;
import com.google.common.collect.SetMultimap;
import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InternalEntityAttribute
implements Serializable {
    private Long id;
    private String name;
    private String value;
    private String lowerValue;

    protected InternalEntityAttribute() {
    }

    public InternalEntityAttribute(String name, String value) {
        Validate.notNull((Object)name, (String)"name cannot be null", (Object[])new Object[0]);
        InternalEntityUtils.validateLength((String)name);
        Validate.notNull((Object)value, (String)"value cannot be null", (Object[])new Object[0]);
        this.name = name;
        this.value = InternalEntityUtils.truncateValue((String)value);
        this.lowerValue = IdentifierUtils.toLowerCase((String)this.value);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    private String getLowerValue() {
        return this.lowerValue;
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        if (value != null) {
            this.value = value;
            this.lowerValue = IdentifierUtils.toLowerCase((String)value);
        }
    }

    private void setLowerValue(String lowerValue) {
        this.lowerValue = lowerValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InternalEntityAttribute)) {
            return false;
        }
        InternalEntityAttribute that = (InternalEntityAttribute)o;
        if (this.getLowerValue() != null ? !this.getLowerValue().equals(that.getLowerValue()) : that.getLowerValue() != null) {
            return false;
        }
        return !(this.getName() != null ? !this.getName().equals(that.getName()) : that.getName() != null);
    }

    public int hashCode() {
        int result = this.getName() != null ? this.getName().hashCode() : 0;
        result = 31 * result + (this.getLowerValue() != null ? this.getLowerValue().hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.id).append("name", (Object)this.name).append("value", (Object)this.value).append("lowerValue", (Object)this.lowerValue).toString();
    }

    public static SetMultimap<String, String> toMap(Collection<? extends InternalEntityAttribute> attributesList) {
        return (SetMultimap)attributesList.stream().collect(CollectorsUtil.toImmutableSetMultiMap(InternalEntityAttribute::getName, InternalEntityAttribute::getValue));
    }
}

