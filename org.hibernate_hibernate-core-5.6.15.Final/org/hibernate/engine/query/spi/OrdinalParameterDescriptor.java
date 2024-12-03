/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.query.spi;

import org.hibernate.Incubating;
import org.hibernate.engine.query.spi.AbstractParameterDescriptor;
import org.hibernate.type.Type;

@Incubating
public class OrdinalParameterDescriptor
extends AbstractParameterDescriptor {
    private final int label;
    private final int valuePosition;

    public OrdinalParameterDescriptor(int label, int valuePosition, Type expectedType, int[] sourceLocations) {
        super(sourceLocations, expectedType);
        this.label = label;
        this.valuePosition = valuePosition;
    }

    @Override
    public Integer getPosition() {
        return this.label;
    }

    public int getValuePosition() {
        return this.valuePosition;
    }
}

