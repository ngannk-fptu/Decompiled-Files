/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import org.hibernate.Incubating;
import org.hibernate.engine.query.spi.AbstractParameterDescriptor;
import org.hibernate.type.Type;

@Incubating
public class NamedParameterDescriptor
extends AbstractParameterDescriptor {
    private final String name;

    public NamedParameterDescriptor(String name, Type expectedType, int[] sourceLocations) {
        super(sourceLocations, expectedType);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NamedParameterDescriptor that = (NamedParameterDescriptor)o;
        return this.getName().equals(that.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }
}

