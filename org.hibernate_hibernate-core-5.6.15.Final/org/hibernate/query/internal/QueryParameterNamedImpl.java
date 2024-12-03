/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.internal;

import org.hibernate.query.QueryParameter;
import org.hibernate.query.internal.QueryParameterImpl;
import org.hibernate.type.Type;

public class QueryParameterNamedImpl<T>
extends QueryParameterImpl<T>
implements QueryParameter<T> {
    private final String name;
    private final int[] sourceLocations;

    public QueryParameterNamedImpl(String name, int[] sourceLocations, Type expectedType) {
        super(expectedType);
        this.name = name;
        this.sourceLocations = sourceLocations;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPosition() {
        return null;
    }

    @Override
    public int[] getSourceLocations() {
        return this.sourceLocations;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        QueryParameterNamedImpl that = (QueryParameterNamedImpl)o;
        return this.getName().equals(that.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }
}

