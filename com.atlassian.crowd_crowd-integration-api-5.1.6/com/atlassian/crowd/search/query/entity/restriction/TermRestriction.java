/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.search.query.entity.restriction;

import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TermRestriction<T>
implements PropertyRestriction<T> {
    private final Property<T> property;
    private final MatchMode matchMode;
    private final T value;

    public TermRestriction(Property<T> property, MatchMode matchMode, T value) {
        this.property = property;
        this.matchMode = matchMode;
        this.value = value;
    }

    public TermRestriction(Property<T> property, T value) {
        this(property, MatchMode.EXACTLY_MATCHES, value);
    }

    @Override
    public final T getValue() {
        return this.value;
    }

    @Override
    public final Property<T> getProperty() {
        return this.property;
    }

    @Override
    public final MatchMode getMatchMode() {
        return this.matchMode;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyRestriction)) {
            return false;
        }
        PropertyRestriction that = (PropertyRestriction)o;
        if (this.matchMode != that.getMatchMode()) {
            return false;
        }
        if (this.property != null ? !this.property.equals(that.getProperty()) : that.getProperty() != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(that.getValue()) : that.getValue() != null);
    }

    public int hashCode() {
        int result = this.property != null ? this.property.hashCode() : 0;
        result = 31 * result + (this.matchMode != null ? this.matchMode.hashCode() : 0);
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("property", this.property).append("matchMode", (Object)this.matchMode).append("value", this.value).toString();
    }
}

