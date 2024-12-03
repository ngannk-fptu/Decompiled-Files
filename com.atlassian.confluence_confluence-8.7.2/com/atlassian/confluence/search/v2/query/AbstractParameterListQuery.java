/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class AbstractParameterListQuery<T>
implements SearchQuery {
    protected List<T> parameters;

    protected AbstractParameterListQuery(T parameter) {
        this((Collection<T>)Collections.singleton(parameter));
    }

    protected AbstractParameterListQuery(Collection<T> parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters should not be null");
        }
        if (parameters.isEmpty()) {
            throw new IllegalArgumentException("parameters should not be empty");
        }
        if (parameters.contains(null)) {
            throw new IllegalArgumentException("Null parameter in parameters list");
        }
        this.parameters = Collections.unmodifiableList(new ArrayList<T>(parameters));
    }

    @Override
    public List<T> getParameters() {
        return this.parameters;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        AbstractParameterListQuery other = (AbstractParameterListQuery)obj;
        return new EqualsBuilder().append(this.parameters, other.parameters).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(115, 37).append(this.parameters).toHashCode();
    }
}

