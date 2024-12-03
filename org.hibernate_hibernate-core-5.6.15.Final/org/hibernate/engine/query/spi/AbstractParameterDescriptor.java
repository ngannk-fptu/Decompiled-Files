/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import org.hibernate.Incubating;
import org.hibernate.query.QueryParameter;
import org.hibernate.type.Type;

@Incubating
public abstract class AbstractParameterDescriptor
implements QueryParameter {
    private final int[] sourceLocations;
    private Type expectedType;

    public AbstractParameterDescriptor(int[] sourceLocations, Type expectedType) {
        this.sourceLocations = sourceLocations;
        this.expectedType = expectedType;
    }

    public String getName() {
        return null;
    }

    public Integer getPosition() {
        return null;
    }

    public Class getParameterType() {
        return this.expectedType == null ? null : this.expectedType.getReturnedClass();
    }

    @Override
    public Type getHibernateType() {
        return this.getExpectedType();
    }

    @Override
    public int[] getSourceLocations() {
        return this.sourceLocations;
    }

    public Type getExpectedType() {
        return this.expectedType;
    }

    public void resetExpectedType(Type expectedType) {
        this.expectedType = expectedType;
    }
}

